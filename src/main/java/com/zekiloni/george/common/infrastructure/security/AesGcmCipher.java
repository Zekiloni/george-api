package com.zekiloni.george.common.infrastructure.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM envelope codec compatible with the Web Crypto encrypt/decrypt
 * used by the visitor simulator and operator console.
 *
 * Envelope shape on the wire:
 *   { "$enc": true, "iv": "<base64 12-byte iv>", "ct": "<base64 ciphertext + 16-byte tag>" }
 *
 * Web Crypto's AES-GCM appends the 128-bit auth tag to the ciphertext;
 * {@link Cipher#doFinal(byte[])} with {@code GCMParameterSpec(128, iv)}
 * expects the same layout, so the two ends interoperate without any extra
 * tag-splitting.
 */
public final class AesGcmCipher {

    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private AesGcmCipher() {}

    public static String decrypt(String base64Key, String base64Iv, String base64Ct) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            byte[] iv = Base64.getDecoder().decode(base64Iv);
            byte[] ct = Base64.getDecoder().decode(base64Ct);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("AES-GCM decrypt failed", e);
        }
    }

    public static Envelope encrypt(String base64Key, String plaintext) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(keyBytes, "AES"),
                    new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return new Envelope(Base64.getEncoder().encodeToString(iv),
                    Base64.getEncoder().encodeToString(ct));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("AES-GCM encrypt failed", e);
        }
    }

    public record Envelope(String iv, String ct) {}
}
