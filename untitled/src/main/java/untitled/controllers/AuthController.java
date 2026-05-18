package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.dto.AuthRequest;
import untitled.dto.AuthResponse;
import untitled.dto.RegisterRequest;
import untitled.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Аутентифікація", description = "Ендпоінти для реєстрації нових користувачів та отримання JWT-токенів")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Реєстрація нового клієнта", description = "Створює новий профіль користувача в базі даних та повертає початковий JWT-токен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успішна реєстрація"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації або email вже існує")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Вхід у систему", description = "Перевіряє облікові дані та повертає JWT-токен для доступу до захищених ендпоінтів.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успішна автентифікація"),
            @ApiResponse(responseCode = "401", description = "Неправильний email або пароль")
    })
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}