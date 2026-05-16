package untitled.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import untitled.entities.OrderEntity;
import untitled.repositories.OrderRepository;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderEntity> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId);
    }

    @PostMapping
    public OrderEntity createOrder(@RequestBody OrderEntity order) {
        // У реальному проекті тут має бути сервіс для розрахунку totalAmount
        return orderRepository.save(order);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderEntity> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
