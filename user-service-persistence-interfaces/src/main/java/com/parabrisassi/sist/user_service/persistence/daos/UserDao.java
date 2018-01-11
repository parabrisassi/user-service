package com.parabrisassi.sist.user_service.persistence.daos;

import com.parabrisassi.sist.user_service.models.User;
import com.parabrisassi.sist.user_service.persistence.custom_repositories.ExtendedJpaRepository;

import java.util.Optional;

/**
 * Defines behaviour of the DAO in charge of managing {@link User}s data.
 */
public interface UserDao extends ExtendedJpaRepository<User, Long> {

    /**
     * Retrieves the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @return A <b>nullable</b> {@link Optional} of {@link User}
     * containing the {@link User} with the given {@code username} if it exists, or {@code null} otherwise.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a {@link User} exists with the given {@code username}.
     *
     * @param username The username to check if a {@link User} exists with.
     * @return {@code true} if a {@link User} exists with the given {@code username}, or {@code false} otherwise.
     */
    boolean existsByUsername(String username);
}
