package untitled.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Конфігурація OpenAPI (Swagger) для автоматичної генерації документації REST API.
 * Включає налаштування глобальної схеми авторизації через JWT токени.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bookstore API",
                version = "v1",
                description = "REST API для управління інтернет-магазином книг"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Введіть отриманий JWT токен для доступу до захищених ресурсів"
)
public class SwaggerConfig {
}