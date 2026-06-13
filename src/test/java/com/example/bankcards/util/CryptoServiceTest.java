package com.example.bankcards.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CryptoServiceTest {

    private final CryptoService cryptoService = new CryptoService("test-card-encryption-key");

    @Test
    void encryptsDecryptsAndDoesNotExposePlainNumber() {
        String cardNumber = "4111111111111111";

        String encrypted = cryptoService.encrypt(cardNumber);

        assertThat(encrypted).doesNotContain(cardNumber);
        assertThat(cryptoService.decrypt(encrypted)).isEqualTo(cardNumber);
    }

    @Test
    void hashesSameNumberDeterministically() {
        String firstHash = cryptoService.hashCardNumber("4111111111111111");
        String secondHash = cryptoService.hashCardNumber("4111111111111111");

        assertThat(firstHash).isEqualTo(secondHash);
        assertThat(firstHash).hasSize(64);
    }
}
