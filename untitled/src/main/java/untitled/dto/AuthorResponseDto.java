package untitled.dto;

import lombok.Builder;

@Builder
public record AuthorResponseDto(
        Long authorId,
        String name,
        String bio,
        Integer birthYear
) {}