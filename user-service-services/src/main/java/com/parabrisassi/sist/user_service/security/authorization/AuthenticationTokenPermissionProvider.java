package com.parabrisassi.sist.user_service.security.authorization;

/**
 * Defines behaviour for an object that provides authorization for operating over
 * {@link com.parabrisassi.sist.user_service.models.AuthenticationToken} instances.
 */
public interface AuthenticationTokenPermissionProvider {

    /**
     * Tells whether the currently authenticated {@link com.parabrisassi.sist.user_service.models.User}
     * owns the {@link com.parabrisassi.sist.user_service.models.AuthenticationToken} with the given {@code id},
     * or if it is an admin.
     *
     * @param tokenId The id of the {@link com.parabrisassi.sist.user_service.models.AuthenticationToken}
     *                to be accessed.
     * @return {@code true} if it is the owner of the token, or if it is an admin, or {@code false} otherwise.
     */
    boolean isOwnerOrAdmin(long tokenId);
}
