package org.pra_frentex.auth.adapters.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;

import org.junit.jupiter.api.Test;
import org.pra_frentex.auth.ports.PasswordHasher;

final class Pbkdf2PasswordHasherTest {

    private final PasswordHasher passwordHasher =
            new Pbkdf2PasswordHasher(new SecureRandom(), 10_000);

    @Test
    void shouldMatchThePasswordUsedToCreateTheHash() {
        char[] password = "correct horse battery staple".toCharArray();

        String encodedPassword = passwordHasher.hash(password);

        assertTrue(passwordHasher.matches(password, encodedPassword));
    }

    @Test
    void shouldRejectAnIncorrectPassword() {
        String encodedPassword = passwordHasher.hash("correct password".toCharArray());

        assertFalse(passwordHasher.matches("wrong password".toCharArray(), encodedPassword));
    }

    @Test
    void shouldGenerateADifferentSaltForEachHash() {
        char[] password = "same password".toCharArray();

        String firstHash = passwordHasher.hash(password);
        String secondHash = passwordHasher.hash(password);

        assertNotEquals(firstHash, secondHash);
        assertTrue(passwordHasher.matches(password, firstHash));
        assertTrue(passwordHasher.matches(password, secondHash));
    }

    @Test
    void shouldRejectMalformedOrUnsupportedHashes() {
        assertFalse(passwordHasher.matches("password".toCharArray(), null));
        assertFalse(passwordHasher.matches("password".toCharArray(), "malformed"));
        assertFalse(passwordHasher.matches(
                "password".toCharArray(),
                "pbkdf2-sha256$2000001$AAAAAAAAAAAAAAAAAAAAAA$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        ));
    }

    @Test
    void shouldRejectNullRawPasswords() {
        assertThrows(
                NullPointerException.class,
                () -> passwordHasher.matches((char[]) null, "encoded")
        );
    }
}
