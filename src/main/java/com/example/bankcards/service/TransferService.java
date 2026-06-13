package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    public TransferService(CardRepository cardRepository, TransferRepository transferRepository) {
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request, String username) {
        if (request.fromCardId().equals(request.toCardId())) {
            throw new ConflictException("Cannot transfer to the same card");
        }

        Card firstLocked = findLocked(request.fromCardId().compareTo(request.toCardId()) < 0
                ? request.fromCardId()
                : request.toCardId());
        Card secondLocked = findLocked(firstLocked.getId().equals(request.fromCardId())
                ? request.toCardId()
                : request.fromCardId());
        Card fromCard = firstLocked.getId().equals(request.fromCardId()) ? firstLocked : secondLocked;
        Card toCard = firstLocked.getId().equals(request.toCardId()) ? firstLocked : secondLocked;

        assertOwner(fromCard, username);
        assertOwner(toCard, username);
        assertTransferable(fromCard, "Source card is not active");
        assertTransferable(toCard, "Target card is not active");

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new ConflictException("Insufficient funds");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        Transfer transfer = new Transfer();
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(request.amount());
        return TransferResponse.from(transferRepository.save(transfer));
    }

    private Card findLocked(UUID cardId) {
        return cardRepository.findByIdForUpdate(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    private static void assertOwner(Card card, String username) {
        if (!card.getOwner().getUsername().equals(username)) {
            throw new ForbiddenOperationException("Only own cards are allowed");
        }
    }

    private static void assertTransferable(Card card, String message) {
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new ConflictException(message);
        }
        if (card.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("Invalid card balance");
        }
    }
}
