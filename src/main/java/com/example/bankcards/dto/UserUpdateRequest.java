package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 255) String fullName,
        Role role,
        Boolean enabled,
        @Size(min = 8, max = 100) String password
) {
}
