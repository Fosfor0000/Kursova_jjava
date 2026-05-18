package untitled.dto;

import lombok.Builder;

@Builder
public record CategoryResponseDto(
        Long categoryId,
        String name,
        String description
) {}