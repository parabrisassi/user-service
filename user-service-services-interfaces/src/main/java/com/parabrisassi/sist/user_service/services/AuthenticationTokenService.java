package com.parabrisassi.sist.user_service.services;

import com.parabrisassi.sist.commons.authentication.AuthenticationTokenBlacklistedChecker;
import com.parabrisassi.sist.commons.authentication.TokenData;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines behaviour of the service in charge of managing authentication tokens.
 */
public interface AuthenticationTokenService extends AuthenticationTokenBlacklistedChecker {

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
     * Invalidates an {@link AuthenticationToken}.
     *
     * @param id The id of the {@link AuthenticationToken}.
     */
    void blacklistToken(long id);


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
        /* package */ TokenWrapper(long id, String rawToken) {
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
}
