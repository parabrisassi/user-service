package com.parabrisassi.sist.user_service.security.authorization;

import com.parabrisassi.sist.commons.authentication.TokenException;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.persistence.daos.AuthenticationTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link AuthenticationTokenPermissionProvider}.
 */
@Component("authenticationTokenPermissionProvider")
public class AuthenticationTokenPermissionProviderImpl implements AuthenticationTokenPermissionProvider {

    /**
     * The {@link Logger} object.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenPermissionProviderImpl.class);

    /**
     * An {@link AuthenticationTokenDao} used to check a given
     * {@link com.parabrisassi.sist.user_service.models.AuthenticationToken} ownership
     */
    private final AuthenticationTokenDao authenticationTokenDao;

    public AuthenticationTokenPermissionProviderImpl(AuthenticationTokenDao authenticationTokenDao) {
        this.authenticationTokenDao = authenticationTokenDao;
    }


    @Override
    public boolean isOwnerOrAdmin(long tokenId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (PermissionProviderHelper.isAdmin(authentication)) {
            return true;
        }

        final Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof String)) {
            LOGGER.error("An Authentication instance has reached the service layer " +
                    "having its principal being null or without having a String as a principal.");
            return false;
        }

        return authenticationTokenDao.findById(tokenId)
                .map(AuthenticationToken::getUser)
                .map(User::getUsername)
                .map(username -> username.equals(principal))
                .orElseThrow(() -> new TokenException("Invalid token"));
    }
}
