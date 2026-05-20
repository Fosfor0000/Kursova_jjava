package untitled.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BookRequestDto(
        @NotBlank(message = "Назва книги не може бути порожньою")
        String title,

        @NotNull(message = "ID автора є обов'язковим")
        Long authorId,

        @NotNull(message = "ID категорії є обов'язковим")
        Long categoryId,

        String isbn,

        @NotNull(message = "Ціна є обов'язковою")
        @Min(value = 0, message = "Ціна не може бути від'ємною")
        BigDecimal price,

        @NotNull(message = "Кількість на складі є обов'язковою")
        @Min(value = 0, message = "Кількість не може бути від'ємною")
        Integer stock,

        String description,
        Integer publishedYear
) {}