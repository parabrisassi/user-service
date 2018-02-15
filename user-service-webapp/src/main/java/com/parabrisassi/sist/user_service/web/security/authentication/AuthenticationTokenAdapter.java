package com.parabrisassi.sist.user_service.web.security.authentication;

import com.parabrisassi.sist.commons.roles.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An extension of an {@link AbstractAuthenticationToken}.
 */
/* package */ class AuthenticationTokenAdapter extends AbstractAuthenticationToken {

    /**
     * The username of the user that this token belongs to.
     * Will be considered the principal in this token.
     */
    private final String username;

    /**
     * Constructor.
     *
     * @param username The username of the user that this token belongs to. Will be considered the principal in this token.
     * @param roles    A {@link Set} of {@link Role}s
     *                 to be granted to this {@link org.springframework.security.core.Authentication}.
     */
    /* package */ AuthenticationTokenAdapter(String username, Collection<Role> roles) {
        super(roles.stream().map(Role::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.username = username;
    }

    /**
     * Authenticates this token.
     */
    /* package */ void authenticate() {
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return (CredentialsContainer) () -> {
            // Do nothing
        };
    }

    @Override
    public Object getPrincipal() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("Not yet authenticated");
        }
        return username;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (!authenticated && isAuthenticated()) {
            throw new IllegalStateException("Can't undo authentication");
        }
        super.setAuthenticated(authenticated);
    }
}
