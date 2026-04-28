package com.zekiloni.george.platform.domain.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@UtilityClass
public final class PhoneNumberFileReader {


    /**
     * Reads phone numbers from an InputStream, returning a Stream of unique, non-blank, trimmed lines.
     *
     * @param inputStream the InputStream to read from
     * @return a Stream of unique, non-blank, trimmed lines from the InputStream
     */
    public static Stream<String> streamPhoneNumbers(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader
                .lines()
                .parallel()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .filter(PhoneNumberFileReader::isValidPhoneNumber)
                .distinct()
                .onClose(() -> closeSilently(reader));
    }

    private static boolean isValidPhoneNumber(String s) {
        return s != null && !s.isBlank() && s.matches("[\\d\\s\\-()]+");
    }

    public static Set<String> collectPhoneNumbers(InputStream inputStream) {
        try (Stream<String> stream = streamPhoneNumbers(inputStream)) {
            return stream.collect(Collectors.toSet());
        }
    }

    private static void closeSilently(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            log.warn("Failed to close reader", e);
        }
    }
}