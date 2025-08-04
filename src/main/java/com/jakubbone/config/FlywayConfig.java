package com.jakubbone.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Development-only Flyway migration strategy that cleans the database schema before migrating.
 * This configuration is only active when the 'dev' profile is enabled.
 * To enable the 'dev' profile, set the environment variable SPRING_PROFILES_ACTIVE=dev.
 *
 * WARNING: This is a destructive operation that will delete all data in the database.
 * It is strictly for use in local development environments.
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
