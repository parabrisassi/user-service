package com.parabrisassi.sist.user_service.web.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An implementation of {@link Authentication} which does not support any of the methods of that interface.
 * It is used to pass a raw token (i.e a {@link String} representation of a token)
 * from a {@link TokenAuthenticationFilter} into a {@link TokenAuthenticationProvider}.
 */
/* package */ class RawAuthenticationToken implements Authentication {

    /**
     * The raw token wrapped by this class.
     */
    private final String token;

    /**
     * Constructor.
     *
     * @param token The raw token to be wrapped by this class.
     */
    /* package */ RawAuthenticationToken(String token) {
        this.token = token;
    }

    /**
     * @return The raw token wrapped by this class.
     */
    /* package */ String getToken() {
        return token;
    }


    // ================================================================================
    // Authentication interface methods, which are not supported.
    // ================================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }
}
