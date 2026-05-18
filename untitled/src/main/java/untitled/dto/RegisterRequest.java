package untitled.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ім'я не може бути порожнім")
        String firstName,

        @NotBlank(message = "Прізвище не може бути порожнім")
        String lastName,

        @NotBlank(message = "Email є обов'язковим")
        @Email(message = "Неправильний формат email")
        String email,

        @NotBlank(message = "Пароль є обов'язковим")
        @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
        String password,

        String phone
) {}