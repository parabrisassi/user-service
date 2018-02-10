package com.parabrisassi.sist.user_service.web.controller.dtos.api_errors;

import com.parabrisassi.sist.commons.errors.NotPresentReferenceError;

import java.util.List;

/**
 * Data transfer object for client errors caused by referencing an entity that does not exist.
 */
public final class NotPresentReferenceErrorDto extends EntityErrorDto<NotPresentReferenceError> {

    /**
     * Constructor.
     *
     * @param errors The {@link List} of {@link NotPresentReferenceError}s.
     */
    public NotPresentReferenceErrorDto(List<NotPresentReferenceError> errors) {
        super(ErrorFamily.NOT_PRESENT_REFERENCE, errors);
    }
}
