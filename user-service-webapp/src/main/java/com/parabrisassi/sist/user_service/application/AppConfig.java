package com.parabrisassi.sist.user_service.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Class that provides Spring Beans for app configuration.
 */
@Configuration
@ComponentScan(value = {
        "com.parabrisassi.sist.user_service.web.config",
        "com.parabrisassi.sist.user_service.services",
        "com.parabrisassi.sist.user_service.persistence",
})
public class AppConfig {
}
