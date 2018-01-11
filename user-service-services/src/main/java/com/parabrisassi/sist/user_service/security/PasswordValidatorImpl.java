package com.parabrisassi.sist.user_service.security;

import com.parabrisassi.sist.user_service.error_handling.errros.ValidationError;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationHelper;
import com.parabrisassi.sist.user_service.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Concrete implementation of {@link PasswordValidator}.
 */
@Component
public class PasswordValidatorImpl implements PasswordValidator {


    @Override
    public void validate(CharSequence password) throws ValidationException {
        final List<ValidationError> errors = new LinkedList<>();
        ValidationHelper.stringNotNullAndLengthBetweenTwoValues(password.toString(),
                MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH,
                errors,
                MISSING_PASSWORD, PASSWORD_TOO_SHORT, PASSWORD_TOO_LONG);
        // TODO: validate format
    }

    // ================================
    // Length constants
    // ================================
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = Integer.MAX_VALUE;


    // ================================
    // Validation errors
    // ================================

    private static final ValidationError MISSING_PASSWORD = new ValidationError(ValidationError.ErrorCause.MISSING_VALUE,
            "password", "The password is missing");
    private static final ValidationError PASSWORD_TOO_SHORT = new ValidationError(ValidationError.ErrorCause.ILLEGAL_VALUE, "password",
            "The password is too short.");
    private static final ValidationError PASSWORD_TOO_LONG = new ValidationError(ValidationError.ErrorCause.ILLEGAL_VALUE, "password",
            "The password is too long.");
}
