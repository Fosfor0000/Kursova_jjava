package untitled.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import untitled.dto.*;
import untitled.entities.*;
import untitled.enums.OrderStatus;
import untitled.exceptions.BusinessException;
import untitled.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
                .orElseThrow(() -> new BusinessException("Користувача не знайдено"));

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
                    .orElseThrow(() -> new BusinessException("Книгу з ID " + itemRequest.getBookId() + " не знайдено"));

            // Перевірка наявності необхідної кількості товару на складі
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
        return OrderResponseDto.builder()
                .orderId(savedOrder.getOrderId())
                .customerName(savedOrder.getCustomer().getFirstName() + " " + savedOrder.getCustomer().getLastName())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .orderDate(savedOrder.getOrderDate())
                .build();
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Замовлення з ID " + orderId + " не знайдено"));

        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);

        return OrderResponseDto.builder()
                .orderId(savedOrder.getOrderId())
                .customerName(savedOrder.getCustomer().getFirstName() + " " + savedOrder.getCustomer().getLastName())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .orderDate(savedOrder.getOrderDate())
                .build();
    }
}