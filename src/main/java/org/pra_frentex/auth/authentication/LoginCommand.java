package org.pra_frentex.auth.authentication;

import java.util.Locale;
import java.util.regex.Pattern;

public final class LoginCommand {

    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_PASSWORD_LENGTH = 1_024;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@"
                    + "[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?"
                    + "(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$",
            Pattern.CASE_INSENSITIVE
    );

    private final String email;
    private final String password;

    public LoginCommand(String email, String password) {
        this.email = validateAndNormalizeEmail(email);
        this.password = validatePassword(password);
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    private static String validateAndNormalizeEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }

        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);

        if (normalizedEmail.isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (normalizedEmail.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException(
                    "Email must not exceed " + MAX_EMAIL_LENGTH + " characters"
            );
        }
        if (hasInvalidDotPlacement(normalizedEmail)
                || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }

        return normalizedEmail;
    }

    private static boolean hasInvalidDotPlacement(String email) {
        int atIndex = email.indexOf('@');
        return email.startsWith(".")
                || email.contains("..")
                || (atIndex > 0 && email.charAt(atIndex - 1) == '.');
    }

    private static String validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must not exceed " + MAX_PASSWORD_LENGTH + " characters"
            );
        }

        return password;
    }
}
