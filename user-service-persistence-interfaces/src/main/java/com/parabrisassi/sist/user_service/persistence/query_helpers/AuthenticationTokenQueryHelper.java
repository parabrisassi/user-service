package com.parabrisassi.sist.user_service.persistence.query_helpers;

import com.parabrisassi.sist.user_service.exceptions.InvalidPropertiesException;
import com.parabrisassi.sist.user_service.models.AuthenticationToken;
import org.springframework.data.domain.Pageable;

/**
 * Defines behaviour of an object in charge of helping the task of querying {@link AuthenticationToken}s
 * by a {@link com.parabrisassi.sist.user_service.persistence.daos.AuthenticationTokenDao}.
 */
public interface AuthenticationTokenQueryHelper {

    /**
     * Validates that the given {@link Pageable} is valid for querying {@link AuthenticationToken}s.
     *
     * @param pageable The {@link Pageable} to be validated.
     * @throws InvalidPropertiesException If it has a {@link org.springframework.data.domain.Sort}
     *                                    with invalid properties.
     */
    void validatePageable(Pageable pageable) throws InvalidPropertiesException;
}
