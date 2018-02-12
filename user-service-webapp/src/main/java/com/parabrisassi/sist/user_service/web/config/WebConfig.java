package com.parabrisassi.sist.user_service.web.config;

import com.bellotapps.utils.error_handler.EnableErrorHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class in charge of configuring web concerns.
 */
@Configuration
@ComponentScan({
        "com.parabrisassi.sist.user_service.web.controller",
})
@EnableErrorHandler(basePackages = {
        "com.parabrisassi.sist.commons.error_handlers",
})
public class WebConfig {
}
