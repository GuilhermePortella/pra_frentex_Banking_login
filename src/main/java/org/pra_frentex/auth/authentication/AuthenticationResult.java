package org.pra_frentex.auth.authentication;

import org.pra_frentex.auth.domain.UserId;

public sealed interface AuthenticationResult {

    record Success(UserId userId)
            implements AuthenticationResult {}

    record InvalidCredentials()
            implements AuthenticationResult {}

    record AccountNotActive()
            implements AuthenticationResult {}
}
