package org.pra_frentex.auth.authentication;

import java.util.Objects;

import org.pra_frentex.auth.ports.PasswordHasher;
import org.pra_frentex.auth.ports.UserRepository;

public final class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthenticationService(
        UserRepository userRepository, 
        PasswordHasher passwordHasher) 
    {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    public AuthenticationResult authenticate(LoginCommand command) {

        var user = userRepository
                .findByEmail(command.email())
                .orElse(null);

        if (user == null) {
            return new AuthenticationResult.InvalidCredentials();
        }

        if (!passwordHasher.matches(
                command.password(),
                user.passwordHash()
        )) {
            return new AuthenticationResult.InvalidCredentials();
        }

        if (!user.isActive()) {
            return new AuthenticationResult.AccountNotActive();
        }

        return new AuthenticationResult.Success(user.id());
    }
}
