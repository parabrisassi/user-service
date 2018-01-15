package com.parabrisassi.sist.user_service.web.security.authentication;

import com.parabrisassi.sist.user_service.services.AuthenticationTokenService;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService.TokenData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * {@link AuthenticationProvider} in charge of performing token authentication.
 */
@Component
public final class TokenAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationTokenService authenticationTokenService;

    @Autowired
    public TokenAuthenticationProvider(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "The authentication must not be null");
        Assert.isInstanceOf(RawAuthenticationToken.class, authentication,
                "The authentication must be a RawAuthenticationToken");

        final RawAuthenticationToken rawAuthenticationToken = (RawAuthenticationToken) authentication;
        try {
            // Performs all validations to the token
            final TokenData tokenData = authenticationTokenService.fromEncodedToken(rawAuthenticationToken.getToken());

            // We create a new token with the needed data (username, roles, etc.)
            final AuthenticationTokenAdapter resultToken =
                    new AuthenticationTokenAdapter(tokenData.getUsername(), tokenData.getRoles());
            resultToken.authenticate();

            return resultToken;
        } catch (AuthenticationTokenService.TokenException e) {
            throw new FailedTokenAuthenticationException(e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RawAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
