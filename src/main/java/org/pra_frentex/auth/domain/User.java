package org.pra_frentex.auth.domain;

import java.util.Objects;

public final class User {

    private final UserId id;
    private final String email;
    private final String passwordHash;
    private final UserStatus status;

    public User(
            UserId id,
            String email,
            String passwordHash,
            UserStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.status = Objects.requireNonNull(status);
    }

    public UserId id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserStatus status() {
        return status;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
