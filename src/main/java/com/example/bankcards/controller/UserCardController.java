package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
public class UserCardController {

    private final CardService cardService;

    public UserCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public PageResponse<CardResponse> listOwn(@RequestParam(required = false) CardStatus status,
                                              @RequestParam(required = false) String search,
                                              @PageableDefault(size = 20) Pageable pageable,
                                              Principal principal) {
        return cardService.listOwn(principal.getName(), status, search, pageable);
    }

    @GetMapping("/{id}")
    public CardResponse getOwn(@PathVariable UUID id, Principal principal) {
        return cardService.getOwn(id, principal.getName());
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable UUID id, Principal principal) {
        return cardService.getBalance(id, principal.getName());
    }

    @PatchMapping("/{id}/request-block")
    public CardResponse requestBlock(@PathVariable UUID id, Principal principal) {
        return cardService.requestBlock(id, principal.getName());
    }
}
