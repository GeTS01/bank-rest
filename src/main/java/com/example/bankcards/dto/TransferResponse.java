package com.example.bankcards.dto;

import com.example.bankcards.entity.Transfer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID id,
        UUID fromCardId,
        UUID toCardId,
        BigDecimal amount,
        Instant createdAt
) {
    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getFromCard().getId(),
                transfer.getToCard().getId(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }
}
