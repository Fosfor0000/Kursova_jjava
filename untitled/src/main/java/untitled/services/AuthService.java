package untitled.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import untitled.dto.AuthRequest;
import untitled.dto.AuthResponse;
import untitled.dto.RegisterRequest;
import untitled.entities.CustomerEntity;
import untitled.exceptions.BusinessException;
import untitled.repositories.CustomerRepository;
import untitled.security.JwtService;
import untitled.enums.Role;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Користувач з таким email вже існує");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPassword(passwordEncoder.encode(request.password())); // Хешування пароля для безпечного зберігання
        customer.setPhone(request.phone());
        customer.setRole(Role.USER);

        customerRepository.save(customer);

        String jwtToken = jwtService.generateToken(customer);
        return new AuthResponse(jwtToken, customer.getCustomerId(), customer.getRole().name());
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        CustomerEntity customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Користувача не знайдено"));

        String jwtToken = jwtService.generateToken(customer);
        return new AuthResponse(jwtToken, customer.getCustomerId(), customer.getRole().name());
    }
}