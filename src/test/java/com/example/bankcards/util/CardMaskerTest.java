package com.example.bankcards.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CardMaskerTest {

    @Test
    void masksCardNumberByLastFourDigits() {
        assertThat(CardMasker.lastFour("4111111111111234")).isEqualTo("1234");
        assertThat(CardMasker.maskLastFour("1234")).isEqualTo("**** **** **** 1234");
    }
}
