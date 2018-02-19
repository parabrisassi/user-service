package com.parabrisassi.sist.user_service.web.config;

import com.parabrisassi.sist.commons.config.EnableJerseyApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class in charge of configuring web concerns.
 */
@Configuration
@ComponentScan({
        "com.parabrisassi.sist.user_service.web.controller",
})
@EnableJerseyApplication(basePackages = {
        "com.parabrisassi.sist.user_service.web.controller.rest_endpoints",
        "com.parabrisassi.sist.commons.data_transfer.jersey_providers",
}, errorHandlersPackages = {
        "com.parabrisassi.sist.commons.error_handlers",
})
public class WebConfig {
}
