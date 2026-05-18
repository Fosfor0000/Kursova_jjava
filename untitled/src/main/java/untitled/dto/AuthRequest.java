package untitled.dto;

public record AuthRequest(
        String email,
        String password
) {}