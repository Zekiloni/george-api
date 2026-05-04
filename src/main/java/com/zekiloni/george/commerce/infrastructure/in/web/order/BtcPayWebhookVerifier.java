package com.zekiloni.george.commerce.infrastructure.in.web.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Verifies BTCPay webhook signatures.
 *
 * <p>BTCPay sends {@code BTCPay-Sig: sha256=<hex>} where the digest is HMAC-SHA256
 * of the raw request body using the per-webhook secret configured in BTCPay's UI.
 * Signature comparison is constant-time to prevent timing attacks.
 *
 * <p>If {@code btc-pay.webhook.secret} is unset/blank, verification is skipped and
 * a warning is logged — useful for local dev without BTCPay's secret configured.
 */
@Component
@Slf4j
public class BtcPayWebhookVerifier {

    private static final String HEADER_PREFIX = "sha256=";

    private final String webhookSecret;

    public BtcPayWebhookVerifier(@Value("${btc-pay.webhook.secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean verify(String signatureHeader, String rawBody) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("btc-pay.webhook.secret is not configured — skipping signature verification (dev only)");
            return true;
        }
        if (signatureHeader == null || rawBody == null) return false;
        if (!signatureHeader.startsWith(HEADER_PREFIX)) return false;
        String providedHex = signatureHeader.substring(HEADER_PREFIX.length()).trim();

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computed = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            byte[] provided = hexToBytes(providedHex);
            return MessageDigest.isEqual(computed, provided);
        } catch (Exception e) {
            log.warn("BTCPay webhook signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) return new byte[0];
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) return new byte[0];
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
