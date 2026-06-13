package com.example.bankcards.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CryptoService {

    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec keySpec;

    public CryptoService(@Value("${app.security.encryption-key}") String encryptionKey) {
        byte[] digest = sha256Bytes(encryptionKey);
        this.keySpec = new SecretKeySpec(Arrays.copyOf(digest, 32), "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder()
                    .encodeToString(ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array());
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Cannot encrypt card number", ex);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] payload = Base64.getDecoder().decode(encryptedText);
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Cannot decrypt card number", ex);
        }
    }

    public String hashCardNumber(String cardNumber) {
        return HexFormat.of().formatHex(sha256Bytes(cardNumber));
    }

    private static byte[] sha256Bytes(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
