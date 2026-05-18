package untitled.dto;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long bookId;
    private Integer quantity;
}