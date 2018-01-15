package com.parabrisassi.sist.user_service.services;

import com.parabrisassi.sist.user_service.models.Role;
import com.parabrisassi.sist.user_service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

/**
 * Defines behaviour of the service in charge of managing {@link User}s.
 */
public interface UserService {

    /**
     * Finds stored {@link User}s, applying optional filters and pagination.
     * String filters are compared with the "like" keyword, matching anywhere.
     *
     * @param username A filter for the {@link User}'s username.
     * @param pageable An {@link Object} implementing the {@link Pageable} interface.
     * @return The resulting {@link Page}.
     * @apiNote Those parameter that are {@code null} must not be taken into account (they are optional).
     */
    Page<User> findMatching(String username, Pageable pageable);


    /**
     * Retrieves the {@link User} with the given {@code username}.
     *
     * @param username The {@link User}'s username.
     * @return A <b>nullable</b> {@link Optional} of {@link User}
     * containing the {@link User} with the given {@code username} if it exists, or {@code null} otherwise.
     */
    Optional<User> getByUsername(String username);

    /**
     * Creates a new {@link User}.
     *
     * @param username The {@link User}'s username.
     * @param password The {@link User}'s password.
     * @return The new {@link User}.
     */
    User register(String username, String password);

    /**
     * Changes the username to the {@link User} with the given {@code oldUsername}.
     *
     * @param oldUsername The actual username of the {@link User} to be altered.
     * @param newUsername The new username.
     */
    void changeUsername(String oldUsername, String newUsername);

    /**
     * Changes the password to the {@link User} with the given {@code username}.
     *
     * @param username        The {@link User}'s username.
     * @param currentPassword The {@link User}'s old password.
     * @param newPassword     The new password.
     */
    void changePassword(String username, String currentPassword, String newPassword);

    /**
     * Retrieves the {@link User}'s with the given {@code username} {@link Role}s.
     *
     * @param username The username of the {@link User} whose roles must be returned.
     * @return The {@link Set} of {@link Role}s of the given {@link User}.
     */
    Set<Role> getRoles(String username);

    /**
     * Adds the given {@link Role} to the {@link User} with th given {@code username}.
     *
     * @param username The username of the {@link User} to which the given {@link Role} must be added.
     * @param role     The {@link Role} to be added.
     * @apiNote This is an idempotent method.
     */
    void addRole(String username, Role role);

    /**
     * Removes the given {@link Role} to the {@link User} with th given {@code username}.
     *
     * @param username The username of the {@link User} to which the given {@link Role} must be removed.
     * @param role     The {@link Role} to be removed.
     * @apiNote This is an idempotent method.
     */
    void removeRole(String username, Role role);

    /**
     * Deletes the {@link User} with the given {@code username}.
     * This is an idempotent operation.
     *
     * @param username The username of the {@link User} to be deleted.
     */
    void deleteByUsername(String username);  // TODO: change to invalidate
}
