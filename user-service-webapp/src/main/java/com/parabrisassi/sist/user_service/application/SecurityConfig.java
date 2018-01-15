package com.parabrisassi.sist.user_service.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security main configuration.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.parabrisassi.sist.user_service.security",
        "com.parabrisassi.sist.user_service.web.security",
})
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
