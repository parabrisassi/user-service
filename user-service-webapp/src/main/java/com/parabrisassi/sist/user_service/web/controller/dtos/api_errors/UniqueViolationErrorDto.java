package com.parabrisassi.sist.user_service.web.controller.dtos.api_errors;

import com.parabrisassi.sist.commons.errors.UniqueViolationError;

import java.util.List;

/**
 * Data transfer object for client errors caused by trying to set a value that is already used and must be unique.
 */
public final class UniqueViolationErrorDto extends EntityErrorDto<UniqueViolationError> {

    /**
     * @param errors The {@link List} of {@link UniqueViolationError}s.
     */
    public UniqueViolationErrorDto(List<UniqueViolationError> errors) {
        super(ErrorFamily.UNIQUE_VIOLATION, errors);
    }
}
