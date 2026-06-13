package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatusRequest;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cards")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.create(request));
    }

    @GetMapping
    public PageResponse<CardResponse> list(@RequestParam(required = false) UUID ownerId,
                                           @RequestParam(required = false) CardStatus status,
                                           @RequestParam(required = false) String search,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return cardService.listAll(ownerId, status, search, pageable);
    }

    @GetMapping("/{id}")
    public CardResponse get(@PathVariable UUID id) {
        return cardService.getForAdmin(id);
    }

    @PatchMapping("/{id}/status")
    public CardResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody CardStatusRequest request) {
        return cardService.updateStatus(id, request.status());
    }

    @PatchMapping("/{id}/block")
    public CardResponse block(@PathVariable UUID id) {
        return cardService.updateStatus(id, CardStatus.BLOCKED);
    }

    @PatchMapping("/{id}/activate")
    public CardResponse activate(@PathVariable UUID id) {
        return cardService.updateStatus(id, CardStatus.ACTIVE);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
