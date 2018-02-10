package com.parabrisassi.sist.user_service.web.error_handlers;

import com.parabrisassi.sist.commons.exceptions.ValidationException;
import com.parabrisassi.sist.user_service.web.Constants;
import com.parabrisassi.sist.user_service.web.controller.dtos.api_errors.ValidationErrorDto;
import com.bellotapps.utils.error_handler.ErrorHandler;
import com.bellotapps.utils.error_handler.ExceptionHandler;
import com.bellotapps.utils.error_handler.ExceptionHandlerObject;

/**
 * {@link ExceptionHandler} in charge of handling {@link ValidationException}.
 * Will result into a <b>422 Unprocessable Entity</b> response.
 */
@ExceptionHandlerObject
/* package */ class ValidationViolationExceptionHandler implements ExceptionHandler<ValidationException> {

    @Override
    public ErrorHandler.HandlingResult handle(ValidationException exception) {
        return new ErrorHandler.HandlingResult(Constants.MissingHttpStatuses.UNPROCESSABLE_ENTITY.getStatusCode(),
                new ValidationErrorDto(exception.getErrors()));
    }
}
