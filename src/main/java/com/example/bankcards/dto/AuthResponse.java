package com.example.bankcards.dto;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
