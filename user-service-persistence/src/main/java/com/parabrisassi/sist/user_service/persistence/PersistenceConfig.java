package com.parabrisassi.sist.user_service.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class to set up persistence layer.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "com.parabrisassi.sist.user_service.persistence.daos",
})
@EntityScan("com.parabrisassi.sist.user_service.models")
public class PersistenceConfig {
}
