package com.parabrisassi.sist.user_service.security.authorization;

import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;

/**
 * Defines behaviour for an object that provides authorization for operating over {@link User} instances.
 */
public interface UserPermissionProvider {

    /**
     * Tells whether the currently authenticated {@link User}
     * can read the {@link User} with the given {@code username}.
     *
     * @param username The username of the {@link User} to be read.
     * @return {@code true} if it has permission, or {@code false} otherwise.
     */
    boolean readByUsername(String username);

    /**
     * Tells whether the currently authenticated {@link User}
     * can write the {@link User} with the given {@code id}.
     *
     * @param username The username of the {@link User} to be written.
     * @return {@code true} if it has permission, or {@code false} otherwise.
     */
    boolean writeByUsername(String username);

    /**
     * Tells whether the currently authenticated {@link User}
     * can delete the {@link User} with the given {@code username}.
     *
     * @param username The username of the {@link User}
     *                 to be deleted.
     * @return {@code true} if it has permission, or {@code false} otherwise.
     */
    boolean deleteByUsername(String username);

    /**
     * Tells whether the currently authenticated {@link User}
     * is admin (i.e (i.e has {@link Role#ROLE_ADMIN} role)
     *
     * @return {@code true} if it is admin, or {@code false} otherwise.
     */
    boolean isAdmin();
}
