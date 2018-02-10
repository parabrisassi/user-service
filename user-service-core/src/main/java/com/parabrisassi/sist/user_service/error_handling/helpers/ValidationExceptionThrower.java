package com.parabrisassi.sist.user_service.error_handling.helpers;

import com.parabrisassi.sist.commons.errors.ValidationError;
import com.parabrisassi.sist.commons.exceptions.ValidationException;

import java.util.List;
import java.util.Objects;

/**
 * An interface implementing a default method used to throw a {@link ValidationException}.
 * This allows reusing the method in an aspect oriented way.
 */
public interface ValidationExceptionThrower {

    /**
     * Throws a {@link ValidationException} if the given {@code errorList} is not empty.
     *
     * @param errorList A {@link List} that might contain {@link ValidationError}s
     *                  that were detected while validating changes over an entity.
     */
    default void throwValidationException(List<ValidationError> errorList) {
        Objects.requireNonNull(errorList, "The error list must not be null");
        if (!errorList.isEmpty()) {
            throw new ValidationException(errorList);
        }
    }

}
