package com.zekiloni.george.platform.domain.lead.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public final class PhoneNumberFileReader {

    /**
     * Reads phone numbers from InputStream (one per line) and returns a Set of cleaned phone numbers.
     *
     * @param inputStream the uploaded file stream
     * @return Set of phone numbers (trimmed, empty lines and duplicates removed)
     * @throws IOException if reading fails
     */
    public static Set<String> readPhoneNumbers(InputStream inputStream) throws IOException {
        Set<String> phoneNumbers = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String phone = line.trim();

                if (!phone.isBlank()) {
                    phoneNumbers.add(phone);
                }
            }
        }

        return phoneNumbers;
    }
}