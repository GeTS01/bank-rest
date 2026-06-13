package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(
        UUID cardId,
        String maskedNumber,
        BigDecimal balance
) {
}
