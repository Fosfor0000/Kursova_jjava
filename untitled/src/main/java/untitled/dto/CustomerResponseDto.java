package untitled.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CustomerResponseDto(
        Long customerId,
        String firstName,
        String lastName,
        String email,
        String role,
        String phone,
        LocalDateTime createdAt
) {}