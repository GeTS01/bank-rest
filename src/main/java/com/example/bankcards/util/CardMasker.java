package com.example.bankcards.util;

public final class CardMasker {

    private CardMasker() {
    }

    public static String maskLastFour(String lastFour) {
        return "**** **** **** " + lastFour;
    }

    public static String lastFour(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
