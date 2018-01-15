package com.parabrisassi.sist.user_service.web.security.authentication;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception to be thrown in case the authentication is not present, but the authentication is mandatory.
 */
/* package */ class UnsupportedAnonymousAuthenticationException extends AuthenticationException {

    /**
     * Default constructor.
     */
    UnsupportedAnonymousAuthenticationException() {
        super("Authentication is not optional");
    }
}
