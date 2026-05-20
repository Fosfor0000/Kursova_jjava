package untitled.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import untitled.dto.*;
import untitled.entities.*;
import untitled.enums.OrderStatus;
import untitled.exceptions.BusinessException;
import untitled.exceptions.ResourceNotFoundException;
import untitled.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public OrderResponseDto processOrder(OrderRequestDto request, String email) {

        // Отримання профілю клієнта на основі email з JWT-токена
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача не знайдено"));

        // Ініціалізація нового замовлення
        OrderEntity order = new OrderEntity();
        order.setCustomer(customer);
        order.setShippingAddress(request.shippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.ZERO);

        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Обробка кожної позиції кошика
        for (OrderItemRequestDto itemRequest : request.items()) {
            BookEntity book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Книгу з ID " + itemRequest.getBookId() + " не знайдено"));

            // [КРОК 5] Перевірка наявності необхідної кількості товару на складі
            if (book.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("Недостатньо книг на складі для: " + book.getTitle());
            }

            // Списання зарезервованих книг зі складу
            book.setStock(book.getStock() - itemRequest.getQuantity());

            // Розрахунок вартості позиції (BigDecimal використовується для фінансової точності)
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // Фіксація купленої позиції та її ціни на момент оформлення
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(book.getPrice());

            order.getItems().add(orderItem);
        }

        // Збереження підсумкової суми та запис у базу даних
        order.setTotalAmount(totalAmount);
        OrderEntity savedOrder = orderRepository.save(order);

        // Формування DTO-відповіді для клієнта
        return mapToResponseDto(savedOrder);
    }

    // [КРОК 4] Сувора послідовність статусів
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + orderId + " не знайдено"));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return mapToResponseDto(order);
        }

        // FSM: Логіка кінцевого автомата через smart switch cases
        boolean isValidTransition = switch (currentStatus) {
            case CREATED -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!isValidTransition) {
            throw new BusinessException("Порушення логіки бізнес-процесу: неможливо змінити статус з "
                    + currentStatus + " на " + newStatus);
        }

        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);

        return mapToResponseDto(savedOrder);
    }

    // [КРОК 1] Скасування власного замовлення з поверненням на склад
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, String email) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + orderId + " не знайдено"));

        if (!order.getCustomer().getEmail().equals(email)) {
            throw new AccessDeniedException("Ви не маєте прав для скасування цього замовлення!");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Неможливо скасувати замовлення, яке вже оплачене або відправлене!");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Повернення на склад
        for (OrderItemEntity item : order.getItems()) {
            BookEntity book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
        }

        OrderEntity savedOrder = orderRepository.save(order);
        return mapToResponseDto(savedOrder);
    }
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetails(Long orderId, String email) {
        // 1. Шукаємо замовлення в базі
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Замовлення з ID " + orderId + " не знайдено"));

        // 2. Шукаємо поточного користувача, який робить запит
        CustomerEntity currentUser = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача не знайдено"));

        // 3. ЗАХИСТ: Звичайний користувач може дивитися тільки СВОЄ замовлення. Адмін — будь-яке.
        if (currentUser.getRole() == untitled.enums.Role.USER && !order.getCustomer().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("Ви не маєте прав для перегляду цього замовлення!");
        }

        // 4. Мапимо список книг із сутностей у DTO
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .bookId(item.getBook().getBookId())
                        .bookTitle(item.getBook().getTitle())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPrice()) // Ціна, за якою купили тоді
                        .build())
                .toList();

        // 5. Збираємо повну відповідь
        return OrderDetailResponseDto.builder()
                .orderId(order.getOrderId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .shippingAddress(order.getShippingAddress())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(itemDtos)
                .build();
    }

    // Допоміжний метод для уникнення дублювання коду
    private OrderResponseDto mapToResponseDto(OrderEntity order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .build();
    }
}