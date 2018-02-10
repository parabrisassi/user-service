package com.parabrisassi.sist.user_service.models;

import com.parabrisassi.sist.commons.errors.ValidationError;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationExceptionThrower;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationHelper;
import com.parabrisassi.sist.user_service.exceptions.ValidationException;
import com.parabrisassi.sist.user_service.models.constants.ValidationConstants;
import com.parabrisassi.sist.user_service.models.constants.ValidationErrorConstants;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing a user of the application.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "users_username_unique_index", columnList = "username", unique = true),
})
public class User implements ValidationExceptionThrower {

    /**
     * The user's id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * The username.
     */
    @Column(name = "username")
    private String username;

    /**
     * The user's authorities.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    /* package */ User() {
        // For Hibernate.
    }

    /**
     * Constructor.
     *
     * @param username The username.
     * @throws ValidationException In case any value is not a valid one.
     */
    public User(String username)
            throws ValidationException {
        final List<ValidationError> errorList = new LinkedList<>();
        changeUsername(username, errorList);
        throwValidationException(errorList); // Throws ValidationException if values were not valid
        this.roles = Stream.of(Role.ROLE_USER).collect(Collectors.toSet());
    }


    /**
     * @return The user's id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return The user's authorities.
     */
    public Set<Role> getRoles() {
        return this.roles;
    }

    /**
     * Changes this user's username.
     *
     * @param username The new username.
     */
    public void changeUsername(String username) {
        final List<ValidationError> errorList = new LinkedList<>();
        changeUsername(username, errorList);
        throwValidationException(errorList); // Throws ValidationException if the username was not valid
    }

    /**
     * Adds the given {@code role} to this user's list of roles.
     *
     * @param role The {@link Role} to be added.
     * @apiNote This is an idempotent operation (i.e adding twice the same role is the same as adding it once).
     */
    public void addRole(Role role) {
        final List<ValidationError> errorList = new LinkedList<>();
        validateRole(role, errorList);
        throwValidationException(errorList);

        this.roles.add(role);
    }

    /**
     * Removes the given {@code role} to this user's list of roles.
     *
     * @param role The {@link Role} to be removed.
     * @apiNote This is an idempotent operation (i.e removing twice the same role is the same as removing it once).
     */
    public void removeRole(Role role) {
        final List<ValidationError> errorList = new LinkedList<>();
        validateRole(role, errorList);
        throwValidationException(errorList);

        this.roles.remove(role);
    }


    // ================================
    // equals, hashcode and toString
    // ================================

    /**
     * Equals based on the {@code id}.
     *
     * @param o The object to be compared with.
     * @return {@code true} if they are the equals, or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return id == user.id;
    }

    /**
     * @return This user's hashcode, based on the {@code id}.
     */
    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "User: [" +
                "ID: " + id + ", " +
                "Username: " + username +
                ']';
    }


    // ================================
    // Private setters
    // ================================

    /**
     * Changes this user's username.
     *
     * @param username  The new username.
     * @param errorList A {@link List} of {@link ValidationError} that might have occurred before executing the method.
     */
    private void changeUsername(String username, List<ValidationError> errorList) {
        setIfNoErrors(this, username, errorList, User::validateUsername,
                (user, newValue) -> user.username = newValue);
    }


    // ================================
    // Helpers
    // ================================

    /**
     * Changes the given {@code editedUser} according to the given {@code setterAction},
     * only if the given {@code validator} did not add {@link ValidationError}s to the given {@code errorList}
     * when validating the given {@code newValue}.
     *
     * @param editedUser   The {@link User} being edited.
     * @param newValue     The new value to set (if no error occurred).
     * @param errorList    A {@link List} containing {@link ValidationError}
     *                     that might have occurred before executing the method.
     *                     It will hold new {@link ValidationError} that can be detected when executing this method.
     * @param validator    A {@link BiConsumer} that takes the given {@code newValue} and {@code errorList}
     *                     in order to validate the former, adding new {@link ValidationError} to the {@link List}
     *                     if errors are detected.
     * @param setterAction A {@link BiConsumer} that takes the given {@link User}, and the given {@code newValue},
     *                     and sets the latter in the former.
     * @param <T>          The concrete type of the {@code newValue}.
     */
    private static <T> void setIfNoErrors(User editedUser, T newValue, List<ValidationError> errorList,
                                          BiConsumer<T, List<ValidationError>> validator,
                                          BiConsumer<User, T> setterAction) {
        Objects.requireNonNull(errorList, "The error list must not be null!");

        final int amountOfErrors = errorList.size();
        validator.accept(newValue, errorList); // If not valid, the size of errorList will increase
        if (errorList.size() <= amountOfErrors) {
            setterAction.accept(editedUser, newValue);
        }
    }


    // ================================
    // Validations
    // ================================

    /**
     * Validates the {@code username}.
     * Will add {@link ValidationError}s to the given {@code errorList} if the {@code username} is not valid.
     *
     * @param username  The username to be validated.
     * @param errorList A {@link List} of {@link ValidationError} that might have occurred before executing the method.
     */
    private static void validateUsername(String username, List<ValidationError> errorList) {
        Objects.requireNonNull(errorList, "The error list must not be null!");
        ValidationHelper.stringNotNullAndLengthBetweenTwoValues(username, ValidationConstants.USERNAME_MIN_LENGTH,
                ValidationConstants.USERNAME_MAX_LENGTH, errorList, ValidationErrorConstants.MISSING_USERNAME,
                ValidationErrorConstants.USERNAME_TOO_SHORT, ValidationErrorConstants.USERNAME_TOO_LONG);
    }

    /**
     * Validates the given {@code role}.
     * Will add {@link ValidationError}s to the given {@code errorList} if the {@code role} is not valid.
     *
     * @param role      The role to be validated.
     * @param errorList A {@link List} of {@link ValidationError} that might have occurred before executing the method.
     */
    private static void validateRole(Role role, List<ValidationError> errorList) {
        Objects.requireNonNull(errorList, "The error list must not be null!");
        ValidationHelper.objectNotNull(role, errorList, ValidationErrorConstants.MISSING_ROLE);
    }
}
