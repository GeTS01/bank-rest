package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMasker;
import com.example.bankcards.util.CryptoService;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CryptoService cryptoService;

    public CardService(CardRepository cardRepository, UserService userService, CryptoService cryptoService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public CardResponse create(CardCreateRequest request) {
        String hash = cryptoService.hashCardNumber(request.cardNumber());
        if (cardRepository.existsByNumberHash(hash)) {
            throw new ConflictException("Card number already exists");
        }

        User owner = userService.getEntity(request.ownerId());
        Card card = new Card();
        card.setEncryptedNumber(cryptoService.encrypt(request.cardNumber()));
        card.setNumberHash(hash);
        card.setLastFour(CardMasker.lastFour(request.cardNumber()));
        card.setOwner(owner);
        card.setExpiryDate(request.expiryDate());
        card.setStatus(resolveStatus(request.expiryDate(), CardStatus.ACTIVE));
        card.setBalance(request.balance());
        return CardResponse.from(cardRepository.save(card));
    }

    @Transactional(readOnly = true)
    public PageResponse<CardResponse> listAll(UUID ownerId, CardStatus status, String search, Pageable pageable) {
        return PageResponse.from(cardRepository.searchAll(ownerId, status, blankToNull(search), pageable).map(CardResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<CardResponse> listOwn(String username, CardStatus status, String search, Pageable pageable) {
        return PageResponse.from(cardRepository.searchOwn(username, status, blankToNull(search), pageable).map(CardResponse::from));
    }

    @Transactional(readOnly = true)
    public CardResponse getForAdmin(UUID id) {
        return CardResponse.from(findById(id));
    }

    @Transactional(readOnly = true)
    public CardResponse getOwn(UUID id, String username) {
        return CardResponse.from(findOwn(id, username));
    }

    @Transactional
    public CardResponse updateStatus(UUID id, CardStatus status) {
        Card card = findById(id);
        card.setStatus(resolveStatus(card.getExpiryDate(), status));
        if (status == CardStatus.BLOCKED) {
            card.setBlockRequested(false);
        }
        return CardResponse.from(card);
    }

    @Transactional
    public CardResponse requestBlock(UUID id, String username) {
        Card card = findOwn(id, username);
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new ConflictException("Expired card cannot be blocked");
        }
        card.setBlockRequested(true);
        return CardResponse.from(card);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID id, String username) {
        Card card = findOwn(id, username);
        return new BalanceResponse(card.getId(), CardMasker.maskLastFour(card.getLastFour()), card.getBalance());
    }

    @Transactional
    public void delete(UUID id) {
        cardRepository.delete(findById(id));
    }

    public Card findById(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card not found"));
        refreshExpiredStatus(card);
        return card;
    }

    public Card findOwn(UUID id, String username) {
        Card card = cardRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        refreshExpiredStatus(card);
        return card;
    }

    private static CardStatus resolveStatus(LocalDate expiryDate, CardStatus requestedStatus) {
        if (expiryDate.isBefore(LocalDate.now())) {
            return CardStatus.EXPIRED;
        }
        return requestedStatus;
    }

    private static void refreshExpiredStatus(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now()) && card.getStatus() != CardStatus.EXPIRED) {
            card.setStatus(CardStatus.EXPIRED);
        }
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}
