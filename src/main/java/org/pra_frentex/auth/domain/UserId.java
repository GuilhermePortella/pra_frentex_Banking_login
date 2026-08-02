package org.pra_frentex.auth.domain;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}
