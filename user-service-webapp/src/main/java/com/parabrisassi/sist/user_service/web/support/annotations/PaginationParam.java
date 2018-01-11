package com.parabrisassi.sist.user_service.web.support.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a parameter contains data for creating a {@link org.springframework.data.domain.Pageable}.
 */
@Target({ElementType.PARAMETER,})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PaginationParam {
}
