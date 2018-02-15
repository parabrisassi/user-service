package com.parabrisassi.sist.user_service.security.authorization;

import com.parabrisassi.sist.commons.roles.Role;
import com.parabrisassi.sist.user_service.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for authorization tasks.
 */
/* package */ class PermissionProviderHelper {

    /**
     * Checks whether the currently authenticated {@link User} is admin (i.e has {@link Role#ROLE_ADMIN} role).
     *
     * @param authentication The {@link Authentication} containing the currently authenticated {@link User} information.
     * @return {@code true} if the currently authenticated {@link User} is admin, or {@code false} otherwise.
     */
    /* package */
    static boolean isAdmin(Authentication authentication) {
        Objects.requireNonNull(authentication, "The authentication must not be null");
        final Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return roles.contains(Role.ROLE_ADMIN.toString());
    }
}
