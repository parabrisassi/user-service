package com.parabrisassi.sist.user_service.security;

import com.parabrisassi.sist.user_service.exceptions.UnauthenticatedException;
import com.parabrisassi.sist.user_service.models.User;

import java.util.Optional;

/**
 * Defines behaviour for an object in charge of providing the current
 * {@link User} {@code id}
 * (i.e the authenticated user in the ongoing request).
 */
public interface CurrentUserIdProvider {


    /**
     * @return A nullable {@link Optional} containing the {@code id}
     * of the current {@link User}
     * (i.e the authenticated user in the ongoing request).
     */
    Optional<Long> currentUserIdOptional();

    /**
     * @return The {@code id} of the current {@link User}
     * (i.e the authenticated user in the on going request).
     * @throws UnauthenticatedException If no {@link User}
     *                                  is authenticated, or if the request is anonymous.
     */
    default long currentUserId() throws UnauthenticatedException {
        return currentUserIdOptional().orElseThrow(UnauthenticatedException::new);
    }
}
