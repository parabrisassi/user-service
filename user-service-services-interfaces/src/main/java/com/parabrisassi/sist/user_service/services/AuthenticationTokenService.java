package com.parabrisassi.sist.user_service.services;

import com.parabrisassi.sist.user_service.exceptions.UnauthenticatedException;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines behaviour of the service in charge of managing authentication tokens.
 */
public interface AuthenticationTokenService {

    /**
     * Retrieves a {@link Page} of {@link TokenData}s belonging to the {@link User}
     * with the given {@code username}, according to the given {@code pageable}.
     *
     * @param username The username of the {@link User} owning the resultant tokens.
     * @param pageable The {@link Pageable} used to set page stuff.
     * @return The resultant {@link Page}.
     */
    Page<TokenData> listTokens(String username, Pageable pageable);

    /**
     * Creates an {@link AuthenticationToken} if the credentials match,
     * and encodes it in a {@link String} representation.
     *
     * @param username The username of the {@link com.parabrisassi.sist.user_service.models.User}
     *                 that will own the created token.
     * @param password The {@link com.parabrisassi.sist.user_service.models.User}'s password
     * @return The created {@link AuthenticationToken}.
     */
    TokenWrapper createToken(String username, String password);

    /**
     * Retrieves an {@link AuthenticationToken} from a {@link String} representation of it.
     *
     * @param encodedToken The encoded {@link AuthenticationToken}.
     * @return The {@link AuthenticationToken} represented by the given {@link String}.
     * @throws TokenException In case the token is not valid.
     */
    TokenData fromEncodedToken(String encodedToken) throws TokenException;

    /**
     * Indicates whether an {@link AuthenticationToken} is valid
     * (i.e not blacklisted and belonging to the {@link User} with the given {@code username}).
     *
     * @param id       The id of the {@link AuthenticationToken}.
     * @param username The username of the user that is performing the request
     *                 (must be the owner of the token to be valid).
     * @return {@code true} if the {@link AuthenticationToken} is valid, or {@code false} otherwise.
     */
    boolean isValidToken(long id, String username);

    /**
     * Invalidates an {@link AuthenticationToken}.
     *
     * @param id The id of the {@link AuthenticationToken}.
     */
    void blacklistToken(long id);

    /**
     * A wrapper class that encapsulates information taken from a token.
     */
    final class TokenData {

        /**
         * The token's id.
         */
        private final long id;

        /**
         * The token's owner username.
         */
        private final String username;

        /**
         * The token's owner roles.
         */
        private final List<Role> roles;

        /**
         * @param id       The token's id.
         * @param username The token's owner username.
         * @param roles    The token's owner roles.
         */
        public TokenData(long id, String username, Collection<Role> roles) {
            this.id = id;
            this.username = username;
            this.roles = new LinkedList<>(roles);
        }

        /**
         * @return The token's id.
         */
        public long getId() {
            return id;
        }

        /**
         * @return The token's owner username.
         */
        public String getUsername() {
            return username;
        }

        /**
         * @return The token's owner roles.
         */
        public List<Role> getRoles() {
            return roles;
        }
    }

    /**
     * Class wrapping a raw token (i.e a {@link String} representation of it), together with its id.
     */
    final class TokenWrapper {

        /**
         * The token id.
         */
        private final long id;

        /**
         * The raw token (i.e a {@link String} representation of it).
         */
        private final String rawToken;

        /**
         * Constructor.
         *
         * @param id       The token id.
         * @param rawToken The raw token (i.e a {@link String} representation of it).
         */
        public TokenWrapper(long id, String rawToken) {
            this.id = id;
            this.rawToken = rawToken;
        }

        /**
         * @return The token id.
         */
        public long getId() {
            return id;
        }

        /**
         * @return The raw token (i.e a {@link String} representation of it).
         */
        public String getRawToken() {
            return rawToken;
        }
    }

    /**
     * Exception to be thrown when there is any problem with a token (i.e decoding, invalid, blacklisted, etc.).
     */
    class TokenException extends UnauthenticatedException {

        /**
         * Default constructor.
         */
        public TokenException() {
            super();
        }

        /**
         * Constructor which can set a {@code message}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         */
        public TokenException(String message) {
            super(message);
        }

        /**
         * Constructor which can set a mes{@code message} and a {@code cause}.
         *
         * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
         * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
         *                For more information, see {@link RuntimeException#RuntimeException(Throwable)}.
         */
        public TokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
