package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.util.CardMasker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardResponse(
        UUID id,
        String maskedNumber,
        UUID ownerId,
        String ownerUsername,
        String ownerFullName,
        LocalDate expiryDate,
        CardStatus status,
        BigDecimal balance,
        boolean blockRequested
) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                CardMasker.maskLastFour(card.getLastFour()),
                card.getOwner().getId(),
                card.getOwner().getUsername(),
                card.getOwner().getFullName(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance(),
                card.isBlockRequested()
        );
    }
}
