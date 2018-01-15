package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService;
import com.parabrisassi.sist.user_service.services.AuthenticationTokenService.TokenData;

/**
 * Defines behaviour of an object that is in charge of encoding/decoding {@link AuthenticationToken}s.
 */
public interface AuthenticationTokenEncoder {

    /**
     * Encodes an {@link AuthenticationToken} into a {@link String}.
     *
     * @param token The token to be encoded.
     * @return An encoded representation of an {@link AuthenticationToken}.
     */
    String encode(TokenData token);

    /**
     * Decodes a {@link String} into an {@link AuthenticationToken}.
     *
     * @param encodedToken A {@link String} representation of an {@link AuthenticationToken}.
     * @return A decoded {@link AuthenticationToken} from the given {@code encodedToken}.
     * @throws TokenDecodingException If the decoding process fails.
     */
    TokenData decode(String encodedToken) throws TokenDecodingException;

    /**
     * Exception thrown when there are token decoding issues.
     */
    final class TokenDecodingException extends AuthenticationTokenService.TokenException {

        /**
         * Default constructor.
         */
        /* package */ TokenDecodingException() {
            super();
        }

        /**
         * Constructor which can set a {@code message}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         */
        /* package */ TokenDecodingException(String message) {
            super(message);
        }

        /**
         * Constructor which can set a mes{@code message} and a {@code cause}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
         *                For more information, see {@link RuntimeException#RuntimeException(Throwable)}.
         */
        /* package */ TokenDecodingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
