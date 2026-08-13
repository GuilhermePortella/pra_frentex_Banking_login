package org.pra_frentex.auth.ports;

import java.util.Arrays;
import java.util.Objects;

public interface PasswordHasher {

    String hash(char[] password);

    boolean matches(char[] rawPassword, String encodedPassword);

    default boolean matches(String rawPassword, String encodedPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        char[] password = rawPassword.toCharArray();
        try {
            return matches(password, encodedPassword);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
