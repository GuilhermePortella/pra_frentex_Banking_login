package org.pra_frentex.auth.ports;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

public interface PasswordHasher {

    String hash(char[] password);

    default boolean matches(String rawPassword, String encodedPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        char[] password = rawPassword.toCharArray();
        try {
            return matches(password, encodedPassword);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    default boolean matches(char[] rawPassword, String encodedPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(encodedPassword, "encodedPassword must not be null");

        String hashedPassword = Objects.requireNonNull(
                hash(rawPassword),
                "hash must not return null"
        );

        return MessageDigest.isEqual(
                hashedPassword.getBytes(StandardCharsets.UTF_8),
                encodedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }
}
