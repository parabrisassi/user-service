package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.commons.errors.ValidationError;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationExceptionThrower;
import com.parabrisassi.sist.user_service.error_handling.helpers.ValidationHelper;
import com.parabrisassi.sist.user_service.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.parabrisassi.sist.commons.errors.ValidationError.ErrorCause.ILLEGAL_VALUE;
import static com.parabrisassi.sist.commons.errors.ValidationError.ErrorCause.MISSING_VALUE;

/**
 * Concrete implementation of {@link PasswordValidator}.
 */
@Component
public class PasswordValidatorImpl implements PasswordValidator, ValidationExceptionThrower {

    @Override
    public void validate(CharSequence password) throws ValidationException {
        final String passwordString = Optional.ofNullable(password)
                .map(CharSequence::toString)
                .orElse(null);
        final List<ValidationError> errors = new LinkedList<>();
        ValidationHelper.stringNotNullAndLengthBetweenTwoValues(passwordString,
                MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH,
                errors,
                MISSING_PASSWORD, PASSWORD_TOO_SHORT, PASSWORD_TOO_LONG);

        // Validate upper case letter
        if (!hasUpperCaseLetter(passwordString)) {
            errors.add(PASSWORD_UPPER_CASE_MISSING);
        }
        // Validate lower case letter
        if (!hasLowerCaseLetter(passwordString)) {
            errors.add(PASSWORD_LOWER_CASE_MISSING);
        }
        // Validate number
        if (!hasNumber(passwordString)) {
            errors.add(PASSWORD_NUMBER_MISSING);
        }
        // Validate special character
        if (!hasSpecialCharacter(passwordString)) {
            errors.add(PASSWORD_SPECIAL_CHARACTER_MISSING);
        }

        throwValidationException(errors); // Throws ValidationException in case the list is not empty
    }

    /**
     * Checks if the given {@code password} has upper case letters.
     *
     * @param password The password to be checked.
     * @return {@code true} if it has upper case letters, or {@code false} otherwise.
     */
    private static boolean hasUpperCaseLetter(String password) {
        return password != null && !password.equals(password.toLowerCase());
    }

    /**
     * Checks if the given {@code password} has lower case letters.
     *
     * @param password The password to be checked.
     * @return {@code true} if it has lower case letters, or {@code false} otherwise.
     */
    private static boolean hasLowerCaseLetter(String password) {
        return password != null && !password.equals(password.toUpperCase());
    }

    /**
     * Checks if the given {@code password} has numbers.
     *
     * @param password The password to be checked.
     * @return {@code true} if it has numbers, or {@code false} otherwise.
     */
    private static boolean hasNumber(String password) {
        return password != null && password.matches(".*\\d.*");
    }

    /**
     * Checks if the given {@code password} has special characters.
     *
     * @param password The password to be checked.
     * @return {@code true} if it has special characters, or {@code false} otherwise.
     */
    private static boolean hasSpecialCharacter(String password) {
        return password != null && password.matches("^.*[^a-zA-Z0-9].*$");
    }

    // ================================
    // Length constants
    // ================================
    /* package */ static final int MIN_PASSWORD_LENGTH = 8;
    /* package */ static final int MAX_PASSWORD_LENGTH = Short.MAX_VALUE;


    // ================================
    // Validation errors
    // ================================

    /* package */ static final ValidationError MISSING_PASSWORD = new ValidationError(MISSING_VALUE, "password",
            "The password is missing");
    /* package */ static final ValidationError PASSWORD_TOO_SHORT = new ValidationError(ILLEGAL_VALUE, "password",
            "The password is too short.");
    /* package */ static final ValidationError PASSWORD_TOO_LONG = new ValidationError(ILLEGAL_VALUE, "password",
            "The password is too long.");

    /* package */ static final ValidationError PASSWORD_UPPER_CASE_MISSING = new ValidationError(ILLEGAL_VALUE,
            "password", "The password is missing an uppercase letter.");
    /* package */ static final ValidationError PASSWORD_LOWER_CASE_MISSING = new ValidationError(ILLEGAL_VALUE,
            "password", "The password is missing a lowercase letter.");
    /* package */ static final ValidationError PASSWORD_NUMBER_MISSING = new ValidationError(ILLEGAL_VALUE,
            "password", "The password is missing a number.");
    /* package */ static final ValidationError PASSWORD_SPECIAL_CHARACTER_MISSING = new ValidationError(ILLEGAL_VALUE,
            "password", "The password is missing a special character.");
}
