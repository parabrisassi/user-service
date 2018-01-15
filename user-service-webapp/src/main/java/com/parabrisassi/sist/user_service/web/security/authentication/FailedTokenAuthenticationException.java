package com.parabrisassi.sist.user_service.web.security.authentication;

import com.parabrisassi.sist.user_service.services.AuthenticationTokenService.TokenException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * {@link AuthenticationException} thrown when there are token issues
 * (e.g invalid token, expired token, blacklisted, etc).
 * <p>
 * This exception acts as a wrapper of {@link TokenException},
 * to be used in the spring security exception handling mechanism.
 */
/* package */ class FailedTokenAuthenticationException extends AuthenticationException {

    /**
     * Constructor which can set a {@code cause}.
     *
     * @param cause The original {@link TokenException} thrown that caused this exception to be created.
     */
    /* package */ FailedTokenAuthenticationException(TokenException cause) {
        this("", cause);
    }

    /**
     * Constructor which can set a mes{@code message} and a {@code cause}.
     *
     * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause   The original {@link TokenException} thrown that caused this exception to be created.
     */
    /* package */ FailedTokenAuthenticationException(String message, TokenException cause) {
        super(message, cause);
        Assert.notNull(cause, "The TokenException must not be null");
    }

    /**
     * @return The original {@link TokenException} thrown that caused this exception to be created.
     */
    /* package */ TokenException getOriginalTokenException() {
        return (TokenException) this.getCause();
    }
}
