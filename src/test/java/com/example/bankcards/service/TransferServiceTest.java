package com.example.bankcards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transfersBetweenOwnActiveCards() {
        User owner = user("alice");
        Card source = card(owner, new BigDecimal("100.00"));
        Card target = card(owner, new BigDecimal("10.00"));
        mockCards(source, target);
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transfer(new TransferRequest(source.getId(), target.getId(), new BigDecimal("25.50")), "alice");

        assertThat(source.getBalance()).isEqualByComparingTo("74.50");
        assertThat(target.getBalance()).isEqualByComparingTo("35.50");
    }

    @Test
    void rejectsTransferFromForeignCard() {
        Card source = card(user("bob"), new BigDecimal("100.00"));
        Card target = card(user("alice"), new BigDecimal("10.00"));
        mockCards(source, target);

        assertThatThrownBy(() -> transferService.transfer(
                new TransferRequest(source.getId(), target.getId(), new BigDecimal("10.00")),
                "alice"
        )).isInstanceOf(ForbiddenOperationException.class);
    }

    @Test
    void rejectsInsufficientFunds() {
        User owner = user("alice");
        Card source = card(owner, new BigDecimal("5.00"));
        Card target = card(owner, BigDecimal.ZERO);
        mockCards(source, target);

        assertThatThrownBy(() -> transferService.transfer(
                new TransferRequest(source.getId(), target.getId(), new BigDecimal("10.00")),
                "alice"
        )).isInstanceOf(ConflictException.class)
                .hasMessageContaining("Insufficient funds");
    }

    private void mockCards(Card source, Card target) {
        when(cardRepository.findByIdForUpdate(source.getId())).thenReturn(Optional.of(source));
        when(cardRepository.findByIdForUpdate(target.getId())).thenReturn(Optional.of(target));
    }

    private static Card card(User owner, BigDecimal balance) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setOwner(owner);
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        card.setLastFour("1234");
        card.setEncryptedNumber("encrypted");
        card.setNumberHash(UUID.randomUUID().toString().replace("-", ""));
        return card;
    }

    private static User user(String username) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword("password");
        user.setFullName(username);
        user.setRole(Role.USER);
        return user;
    }
}
