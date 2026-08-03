package org.pra_frentex.auth.ports;

import java.util.Optional;

import org.pra_frentex.auth.domain.User;
import org.pra_frentex.auth.domain.UserId;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId id);

    void save(User user);
}
