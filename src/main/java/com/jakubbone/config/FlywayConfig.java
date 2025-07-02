package com.jakubbone.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Flyway migration strategy that cleans the schema before migrating
 * Enable only in development by activating the "dev" profile via application.properties:
 *   spring.profiles.active=dev
 * WARNING: This will delete all data! Use only in dev
 */
@Configuration
@Profile("dev")
public class FlywayConfig {
    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
