package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import untitled.dto.OrderDetailResponseDto;
import untitled.dto.OrderRequestDto;
import untitled.dto.OrderResponseDto;
import untitled.entities.CustomerEntity;
import untitled.entities.OrderEntity;
import untitled.repositories.CustomerRepository;
import untitled.repositories.OrderRepository;
import untitled.services.OrderService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "3. Управління замовленнями", description = "Ендпоінти для оформлення покупок та відстеження замовлень")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CustomerRepository customerRepository;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Мої замовлення", description = "Отримання історії замовлень поточного авторизованого користувача (на основі JWT).")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Principal principal) {
        CustomerEntity customer = customerRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        List<OrderResponseDto> myOrders = orderRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .stream()
                .map(this::mapToOrderDto)
                .toList();

        return ResponseEntity.ok(myOrders);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Отримати всі замовлення системи", description = "Адміністративний доступ до повного списку замовлень усіх клієнтів.")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderRepository.findAll().stream()
                .map(this::mapToOrderDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Замовлення конкретного клієнта", description = "Пошук замовлень за ідентифікатором клієнта (тільки для ADMIN).")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(
            @Parameter(description = "ID клієнта") @PathVariable(name = "customerId") Long customerId) {
        List<OrderResponseDto> orders = orderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(this::mapToOrderDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати деталі конкретного замовлення з книжками (Доступно для USER (свого) та ADMIN)")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetails(
            @PathVariable(name = "id") Long id,
            Principal principal) {
        OrderDetailResponseDto response = orderService.getOrderDetails(id, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Оформити замовлення", description = "Створення нового замовлення. Розрахунок вартості та перевірка складських залишків виконуються на сервері.")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto request,
            Principal principal) {
        OrderResponseDto response = orderService.processOrder(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Скасувати власне замовлення (доступно для USER)")
    public ResponseEntity<OrderResponseDto> cancelMyOrder(
            @PathVariable(name = "id") Long id,
            Principal principal) {
        OrderResponseDto response = orderService.cancelOrder(id, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Змінити статус замовлення", description = "Оновлення етапу виконання замовлення (тільки для ADMIN).")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @Parameter(description = "ID замовлення") @PathVariable(name = "id") Long id,
            @Parameter(description = "Новий статус") @RequestParam(name = "status") untitled.enums.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // Допоміжний метод для інкапсуляції логіки мапінгу сутності в DTO
    private OrderResponseDto mapToOrderDto(OrderEntity order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .build();
    }
}