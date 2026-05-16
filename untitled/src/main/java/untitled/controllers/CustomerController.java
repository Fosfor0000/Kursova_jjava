package untitled.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import untitled.entities.CustomerEntity;
import untitled.repositories.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CustomerEntity registerCustomer(@RequestBody CustomerEntity customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerEntity> findByEmail(@RequestParam String email) {
        return customerRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
