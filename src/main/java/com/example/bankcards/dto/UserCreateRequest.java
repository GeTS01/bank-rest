package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 255) String fullName,
        @NotNull Role role
) {
}
