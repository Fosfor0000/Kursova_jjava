package untitled.dto;

import java.util.List;

public record OrderRequestDto(
        String shippingAddress,
        List<OrderItemRequestDto> items
) {}