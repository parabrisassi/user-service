package com.parabrisassi.sist.user_service.models;

import com.parabrisassi.sist.commons.errors.ValidationError;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationExceptionThrower;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationHelper;
import com.parabrisassi.sist.commons.exceptions.ValidationException;
import com.parabrisassi.sist.user_service.models.constants.ValidationErrorConstants;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a user access credential (i.e it's password).
 */
@Entity
@Table(name = "user_credentials")
public class UserCredential implements ValidationExceptionThrower {

    /**
     * The credential id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * The {@link User} owning this credential.
     */
    @JoinColumn(columnDefinition = "integer", name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    /**
     * The hashed password.
     */
    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    /**
     * {@link Instant} at which this credential is created.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /* package */ UserCredential() {
        // For Hibernate
    }

    /**
     * @param user           The {@link User} owning this credential.
     * @param hashedPassword The hashed password.
     */
    public UserCredential(User user, String hashedPassword) {
        if (hashedPassword == null) {
            throw new IllegalArgumentException("The hashed password must not be null");
        }
        validate(user);

        this.user = user;
        this.hashedPassword = hashedPassword;
        this.createdAt = Instant.now();
    }

    /**
     * @return The credential id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The {@link User} owning this credential.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The hashed password.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @return {@link Instant} at which this credential is created.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Validates the given arguments.
     *
     * @param user The {@link User} to be validated.
     * @throws ValidationException If any of the values is not valid.
     */
    private void validate(User user) throws ValidationException {
        final List<ValidationError> errors = new LinkedList<>();
        ValidationHelper.objectNotNull(user, errors, ValidationErrorConstants.MISSING_USER);
        throwValidationException(errors);
    }
}
