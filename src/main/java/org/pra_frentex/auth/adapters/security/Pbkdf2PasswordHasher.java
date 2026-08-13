package org.pra_frentex.auth.adapters.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.pra_frentex.auth.ports.PasswordHasher;

public final class Pbkdf2PasswordHasher implements PasswordHasher {

    private static final String ALGORITHM_ID = "pbkdf2-sha256";
    private static final String JCA_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int DEFAULT_ITERATIONS = 600_000;
    private static final int MAX_ITERATIONS = 2_000_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BYTES = 32;
    private static final int MAX_ENCODED_PASSWORD_LENGTH = 512;

    private final SecureRandom secureRandom;
    private final int iterations;

    public Pbkdf2PasswordHasher() {
        this(new SecureRandom(), DEFAULT_ITERATIONS);
    }

    public Pbkdf2PasswordHasher(SecureRandom secureRandom, int iterations) {
        this.secureRandom = Objects.requireNonNull(secureRandom, "secureRandom must not be null");
        if (iterations <= 0 || iterations > MAX_ITERATIONS) {
            throw new IllegalArgumentException(
                    "iterations must be between 1 and " + MAX_ITERATIONS
            );
        }
        this.iterations = iterations;
    }

    @Override
    public String hash(char[] password) {
        Objects.requireNonNull(password, "password must not be null");

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        secureRandom.nextBytes(salt);
        byte[] derivedKey = deriveKey(password, salt, iterations, HASH_LENGTH_BYTES);

        try {
            return String.join(
                    "$",
                    ALGORITHM_ID,
                    Integer.toString(iterations),
                    Base64.getEncoder().withoutPadding().encodeToString(salt),
                    Base64.getEncoder().withoutPadding().encodeToString(derivedKey)
            );
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(derivedKey, (byte) 0);
        }
    }

    @Override
    public boolean matches(char[] rawPassword, String encodedPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        if (encodedPassword == null || encodedPassword.length() > MAX_ENCODED_PASSWORD_LENGTH) {
            return false;
        }

        ParsedHash parsedHash;
        try {
            parsedHash = parse(encodedPassword);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        byte[] candidate = deriveKey(
                rawPassword,
                parsedHash.salt(),
                parsedHash.iterations(),
                parsedHash.hash().length
        );

        try {
            return MessageDigest.isEqual(candidate, parsedHash.hash());
        } finally {
            Arrays.fill(candidate, (byte) 0);
            parsedHash.clear();
        }
    }

    private static ParsedHash parse(String encodedPassword) {
        String[] parts = encodedPassword.split("\\$", -1);
        if (parts.length != 4 || !ALGORITHM_ID.equals(parts[0])) {
            throw new IllegalArgumentException("Unsupported password hash format");
        }

        int storedIterations;
        try {
            storedIterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid iteration count", exception);
        }
        if (storedIterations <= 0 || storedIterations > MAX_ITERATIONS) {
            throw new IllegalArgumentException("Invalid iteration count");
        }

        byte[] salt = new byte[0];
        byte[] hash = new byte[0];
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            hash = Base64.getDecoder().decode(parts[3]);
            if (salt.length != SALT_LENGTH_BYTES || hash.length != HASH_LENGTH_BYTES) {
                throw new IllegalArgumentException("Invalid salt or hash length");
            }
            return new ParsedHash(storedIterations, salt, hash);
        } catch (IllegalArgumentException exception) {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);
            throw exception;
        }
    }

    private static byte[] deriveKey(
            char[] password,
            byte[] salt,
            int iterations,
            int hashLengthBytes
    ) {
        PBEKeySpec specification = new PBEKeySpec(
                password,
                salt,
                iterations,
                Math.multiplyExact(hashLengthBytes, Byte.SIZE)
        );
        try {
            return SecretKeyFactory.getInstance(JCA_ALGORITHM)
                    .generateSecret(specification)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("PBKDF2-HMAC-SHA-256 is not available", exception);
        } finally {
            specification.clearPassword();
        }
    }

    private record ParsedHash(int iterations, byte[] salt, byte[] hash) {

        private void clear() {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);
        }
    }
}
