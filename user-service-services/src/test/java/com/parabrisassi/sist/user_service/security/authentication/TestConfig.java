package com.parabrisassi.sist.user_service.security.authentication;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Test configuration class for the authentication package (i.e this package).
 */
@Configuration
@ComponentScan(value = "com.parabrisassi.sist.user_service.security.authentication",
        lazyInit = true,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                AuthenticationConfig.class,
                        })
        })
public class TestConfig {
}
