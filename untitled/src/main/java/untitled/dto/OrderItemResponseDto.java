package untitled.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record OrderItemResponseDto(
        Long bookId,
        String bookTitle,
        Integer quantity,
        BigDecimal priceAtPurchase // Фіксуємо ціну на момент купівлі
) {}