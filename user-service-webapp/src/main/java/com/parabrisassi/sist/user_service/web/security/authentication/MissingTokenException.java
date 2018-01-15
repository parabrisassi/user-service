package com.parabrisassi.sist.user_service.web.security.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception to be thrown in case the token is not present.
 */
/* package */ class MissingTokenException extends AuthenticationException {

    /**
     * Default constructor.
     */
    MissingTokenException() {
        super("The token is missing");
    }
}
