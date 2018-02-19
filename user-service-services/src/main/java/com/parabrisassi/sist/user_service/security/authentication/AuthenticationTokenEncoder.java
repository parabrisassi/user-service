package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.commons.authentication.TokenData;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;

/**
 * Defines behaviour of an object that is in charge of encoding {@link AuthenticationToken}s.
 */
public interface AuthenticationTokenEncoder {

    /**
     * Encodes an {@link AuthenticationToken} into a {@link String}.
     *
     * @param token The token to be encoded.
     * @return An encoded representation of an {@link AuthenticationToken}.
     */
    String encode(TokenData token);
}
