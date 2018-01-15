package com.parabrisassi.sist.user_service.models.constants;


import com.parabrisassi.sist.user_service.error_handling.errros.ValidationError;

import static com.parabrisassi.sist.user_service.error_handling.errros.ValidationError.ErrorCause.ILLEGAL_VALUE;
import static com.parabrisassi.sist.user_service.error_handling.errros.ValidationError.ErrorCause.MISSING_VALUE;

/**
 * Class containing {@link ValidationError} constants to be reused.
 */
public class ValidationErrorConstants {

    public static final ValidationError MISSING_USERNAME = new ValidationError(MISSING_VALUE, "username",
            "The username is missing.");
    public static final ValidationError USERNAME_TOO_SHORT = new ValidationError(ILLEGAL_VALUE, "username",
            "The username is too short.");
    public static final ValidationError USERNAME_TOO_LONG = new ValidationError(ILLEGAL_VALUE, "username",
            "The username is too long.");


    public static final ValidationError MISSING_USER = new ValidationError(MISSING_VALUE, "user",
            "The user is missing.");


    public static final ValidationError MISSING_ROLE = new ValidationError(MISSING_VALUE, "role",
            "The role is missing.");
}
