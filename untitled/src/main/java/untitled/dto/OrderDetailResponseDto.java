package untitled.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDetailResponseDto(
        Long orderId,
        String customerName,
        String shippingAddress,
        LocalDateTime orderDate,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponseDto> items // <--- Ось тут список книг!
) {}