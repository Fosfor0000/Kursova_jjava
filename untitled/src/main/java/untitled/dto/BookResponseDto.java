package untitled.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record BookResponseDto(
        Long bookId,
        String title,
        String authorName,   // Віддаємо готове ім'я автора замість ID!
        String categoryName, // Віддаємо готову назву категорії!
        String isbn,
        BigDecimal price,
        Integer stock,
        String description,
        Integer publishedYear
) {}