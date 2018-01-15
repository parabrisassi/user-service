package com.parabrisassi.sist.user_service.persistence;

import com.parabrisassi.sist.user_service.exceptions.InvalidPropertiesException;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import com.parabrisassi.sist.user_service.persistence.query_helpers.AuthenticationTokenQueryHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of a {@link AuthenticationTokenQueryHelper}.
 */
@Component
public class AuthenticationTokenQueryHelperImpl implements AuthenticationTokenQueryHelper {

    @Override
    public void validatePageable(Pageable pageable) throws InvalidPropertiesException {
        PersistenceHelper.validatePageable(pageable, AuthenticationToken.class);
    }
}
