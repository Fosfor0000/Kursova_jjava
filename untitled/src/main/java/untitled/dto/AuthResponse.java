package untitled.dto;

public record AuthResponse(
        String token,
        Long customerId, // Додаємо ID
        String role      // Додаємо роль (щоб фронтенд знав, чи малювати кнопку "Адмінка")
) {}