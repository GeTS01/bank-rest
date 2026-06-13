package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardCreateRequest(
        @NotBlank @Pattern(regexp = "\\d{13,19}", message = "cardNumber must contain 13-19 digits") String cardNumber,
        @NotNull UUID ownerId,
        @NotNull @Future LocalDate expiryDate,
        @NotNull @DecimalMin(value = "0.00") BigDecimal balance
) {
}
