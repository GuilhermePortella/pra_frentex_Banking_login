package org.pra_frentex.auth.authentication;

import org.pra_frentex.auth.domain.UserId;

public sealed interface AuthenticationResult {

    record Success(UserId userId)
            implements AuthenticationResult {}

    record InvalidCredentials()
            implements AuthenticationResult {}

    /** @deprecated Authentication failures now use InvalidCredentials to avoid account disclosure. */
    @Deprecated
    record AccountNotActive()
            implements AuthenticationResult {}
}
