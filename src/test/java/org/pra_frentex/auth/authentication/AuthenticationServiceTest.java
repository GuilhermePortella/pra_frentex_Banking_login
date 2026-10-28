package org.pra_frentex.auth.authentication;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.pra_frentex.auth.domain.*;
import org.pra_frentex.auth.ports.*;

class AuthenticationServiceTest {
    private final TrackingHasher hasher = new TrackingHasher();
    private final LoginCommand command = new LoginCommand("user@example.com", "secret");

    @Test
    void authenticatesActiveUserWithCorrectPassword() {
        User user = user(UserStatus.ACTIVE);
        var service = service(user);
        assertEquals(new AuthenticationResult.Success(user.id()), service.authenticate(command));
        assertEquals("stored-hash", hasher.checkedHash);
        assertEquals(1, hasher.checks);
        assertCleared(hasher.checkedPassword);
    }

    @Test
    void rejectsIncorrectPassword() {
        var service = service(user(UserStatus.ACTIVE));
        hasher.matches = false;
        assertInstanceOf(AuthenticationResult.InvalidCredentials.class, service.authenticate(command));
    }

    @Test
    void rejectsEveryInactiveStatusWithoutDisclosingIt() {
        for (UserStatus status : UserStatus.values()) {
            if (status != UserStatus.ACTIVE) {
                var service = service(user(status));
                assertInstanceOf(AuthenticationResult.InvalidCredentials.class, service.authenticate(command));
                assertEquals("stored-hash", hasher.checkedHash);
            }
        }
    }

    @Test
    void verifiesDummyHashForUnknownAccountEvenIfHasherReturnsTrue() {
        var service = service(null);
        assertInstanceOf(AuthenticationResult.InvalidCredentials.class, service.authenticate(command));
        assertEquals("dummy-hash", hasher.checkedHash);
        assertEquals(1, hasher.checks);
        assertCleared(hasher.checkedPassword);
        service.authenticate(command);
        assertEquals(1, hasher.hashes);
        assertEquals(2, hasher.checks);
    }

    @Test
    void clearsTemporaryDummyPassword() {
        service(null);
        assertCleared(hasher.hashedPassword);
    }

    @Test
    void clearsPasswordWhenHasherFailsAndPropagatesInfrastructureError() {
        var service = service(user(UserStatus.ACTIVE));
        hasher.fail = true;
        assertThrows(IllegalStateException.class, () -> service.authenticate(command));
        assertCleared(hasher.checkedPassword);
    }

    @Test
    void rejectsNullCommandBeforeCheckingPassword() {
        var service = service(null);
        assertThrows(NullPointerException.class, () -> service.authenticate(null));
        assertEquals(0, hasher.checks);
    }

    private static void assertCleared(char[] value) {
        assertNotNull(value);
        assertArrayEquals(new char[value.length], value);
    }

    private User user(UserStatus status) {
        return new User(UserId.generate(), command.email(), "stored-hash", status);
    }

    private AuthenticationService service(User user) {
        return new AuthenticationService(new UserRepository() {
            public Optional<User> findByEmail(String email) {
                assertEquals(command.email(), email);
                return Optional.ofNullable(user);
            }
            public Optional<User> findById(UserId id) { throw new UnsupportedOperationException(); }
            public void save(User value) { throw new UnsupportedOperationException(); }
        }, hasher);
    }

    private static final class TrackingHasher implements PasswordHasher {
        int hashes;
        int checks;
        boolean matches = true;
        boolean fail;
        String checkedHash;
        char[] checkedPassword;
        char[] hashedPassword;

        public String hash(char[] password) {
            hashes++;
            hashedPassword = password;
            return "dummy-hash";
        }

        public boolean matches(char[] password, String hash) {
            checks++;
            checkedPassword = password;
            checkedHash = hash;
            if (fail) throw new IllegalStateException("Hasher unavailable");
            return matches;
        }
    }
}
