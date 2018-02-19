package com.parabrisassi.sist.user_service.security.authentication;

import com.parabrisassi.sist.commons.exceptions.ValidationException;

/**
 * Defines behaviour of an object that is in charge of validating a password.
 */
public interface PasswordValidator {

    /**
     * Checks whether the given {@code password} is valid, throwing a {@link ValidationException} if not.
     *
     * @param password The password to be validated.
     * @throws ValidationException If the given {@code password} is not valid.
     */
    void validate(CharSequence password) throws ValidationException;
}
