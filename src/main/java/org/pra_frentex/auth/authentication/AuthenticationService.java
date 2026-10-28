package org.pra_frentex.auth.authentication;

import java.util.Objects;
import java.util.Arrays;
import java.security.SecureRandom;

import org.pra_frentex.auth.domain.User;
import org.pra_frentex.auth.ports.PasswordHasher;
import org.pra_frentex.auth.ports.UserRepository;

public final class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final String dummyPasswordHash;

    public AuthenticationService(
        UserRepository userRepository, 
        PasswordHasher passwordHasher) 
    {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        // Pay the configured hashing cost once at initialization, not per request.
        char[] dummyPassword = new char[32];
        SecureRandom random = new SecureRandom();
        try {
            for (int i = 0; i < dummyPassword.length; i++) {
                dummyPassword[i] = (char) ('!' + random.nextInt(94));
            }
            this.dummyPasswordHash = Objects.requireNonNull(passwordHasher.hash(dummyPassword));
        } finally {
            Arrays.fill(dummyPassword, '\0');
        }
    }

    public AuthenticationResult authenticate(LoginCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        User user = userRepository
                .findByEmail(command.email())
                .orElse(null);

        boolean passwordMatches = passwordHasher.matches(
                command.password(),
                user == null ? dummyPasswordHash : user.passwordHash()
        );
        if (user == null || !passwordMatches || !user.isActive()) {
            return new AuthenticationResult.InvalidCredentials();
        }

        return new AuthenticationResult.Success(user.id());
    }
}
