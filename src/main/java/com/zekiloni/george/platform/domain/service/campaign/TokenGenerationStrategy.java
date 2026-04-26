package com.zekiloni.george.platform.domain.service.campaign;

import java.security.SecureRandom;

public enum TokenGenerationStrategy {

    UPPERCASE_ALPHA {
        private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        @Override
        public String generate(int length) {
            return random(CHARS, length);
        }
    },

    LOWERCASE_ALPHA {
        private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";
        @Override
        public String generate(int length) {
            return random(CHARS, length);
        }
    },

    NUMERIC {
        private static final String CHARS = "0123456789";
        @Override
        public String generate(int length) {
            return random(CHARS, length);
        }
    },

    ALPHANUMERIC {
        private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        @Override
        public String generate(int length) {
            return random(CHARS, length);
        }
    },

    UPPERCASE_ALPHANUMERIC {
        private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        @Override
        public String generate(int length) {
            return random(CHARS, length);
        }
    },

    UUID_BASED {
        @Override
        public String generate(int length) {
            return java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, length)
                .toUpperCase();
        }
    };

    private static final SecureRandom RANDOM = new SecureRandom();

    public abstract String generate(int length);

    protected String random(String charset, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return sb.toString();
    }
}