package org.pra_frentex.auth.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LoginCommandTest {

    @Test
    void normalizesEmailAndPreservesPassword() {
        LoginCommand command = new LoginCommand(
                "  User.Name+tag@Example.COM  ",
                " password with spaces "
        );

        assertEquals("user.name+tag@example.com", command.email());
        assertEquals(" password with spaces ", command.password());
    }

    @Test
    void rejectsNullAndBlankCredentials() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand(null, "secret"));
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("   ", "secret"));
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("user@example.com", null));
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("user@example.com", "  \t"));
    }

    @Test
    void rejectsMalformedEmails() {
        String[] invalidEmails = {
                "user.example.com",
                "user@@example.com",
                ".user@example.com",
                "user.@example.com",
                "user..name@example.com",
                "user@example",
                "user@-example.com",
                "user@example-.com",
                "user name@example.com"
        };

        for (String email : invalidEmails) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new LoginCommand(email, "secret"),
                    () -> "Expected invalid email to be rejected: " + email
            );
        }
    }

    @Test
    void rejectsExcessivelyLongCredentials() {
        String maximumLengthEmail = "a".repeat(242) + "@example.com";
        String oversizedEmail = "a".repeat(243) + "@example.com";
        String oversizedPassword = "a".repeat(1_025);

        assertEquals(
                maximumLengthEmail,
                new LoginCommand(maximumLengthEmail, "secret").email()
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new LoginCommand(oversizedEmail, "secret")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new LoginCommand("user@example.com", oversizedPassword)
        );
    }
}
