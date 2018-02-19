package com.parabrisassi.sist.user_service.persistence.query_helpers;

import com.parabrisassi.sist.commons.exceptions.InvalidPropertiesException;
import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.persistence.daos.UserDao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Defines behaviour of an object in charge of helping the task of querying {@link User}s
 * by a {@link UserDao}.
 */
public interface UserQueryHelper {

    /**
     * Creates a new {@link Specification} of {@link User} used to query them according to the given parameters,
     * applying ANDs between them.
     * String parameters are compared with the "like" keyword, matching anywhere.
     *
     * @param username A filter for the {@link User}'s username.
     * @return The {@link Specification} of {@link User}
     * that can be used to get those {@link User}s matching the give parameters.
     * @apiNote Those parameter that are {@code null} must not be taken into account in the {@link Specification}.
     */
    Specification<User> createUserSpecification(String username);

    /**
     * Validates that the given {@link Pageable} is valid for querying {@link User}s.
     *
     * @param pageable The {@link Pageable} to be validated.
     * @throws InvalidPropertiesException If it has a {@link org.springframework.data.domain.Sort}
     *                                    with invalid properties.
     */
    void validatePageable(Pageable pageable) throws InvalidPropertiesException;
}
