package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import untitled.dto.CustomerResponseDto;
import untitled.entities.CustomerEntity;
import untitled.repositories.CustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "6. Управління клієнтами", description = "Ендпоінти для перегляду профілів користувачів")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    @Operation(summary = "Отримати всіх клієнтів")
    public ResponseEntity<List<CustomerResponseDto>> getAll() {
        List<CustomerResponseDto> customers = customerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати клієнта за ID")
    public ResponseEntity<CustomerResponseDto> getById(
            @Parameter(description = "ID користувача", required = true)
            @PathVariable(name = "id") Long id) {
        return customerRepository.findById(id)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Допоміжний метод для конвертації
    private CustomerResponseDto mapToDto(CustomerEntity customer) {
        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .role(customer.getRole().name())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}