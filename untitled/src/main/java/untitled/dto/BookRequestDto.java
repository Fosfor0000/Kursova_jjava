package untitled.dto;

import java.math.BigDecimal;

public record BookRequestDto(
        String title,
        Long authorId,      // Приймаємо лише ID автора
        Long categoryId,    // Приймаємо лише ID категорії
        String isbn,
        BigDecimal price,
        Integer stock,
        String description,
        Integer publishedYear,
        String coverImage
) {}