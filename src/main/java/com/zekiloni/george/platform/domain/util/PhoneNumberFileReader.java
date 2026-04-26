package com.zekiloni.george.platform.domain.util;

import lombok.experimental.UtilityClass;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@UtilityClass
public final class PhoneNumberFileReader {

    /**
     * Reads phone numbers from an input stream, ensuring uniqueness and trimming whitespace.
     * @param inputStream
     * @return
     */
    public static Stream<String> streamPhoneNumbers(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .distinct()
                .onClose(() -> {
                    try { reader.close(); } catch (IOException e) { throw new UncheckedIOException(e); }
                });
    }
}