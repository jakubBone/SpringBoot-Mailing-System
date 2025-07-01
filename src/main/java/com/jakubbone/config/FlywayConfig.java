package com.jakubbone.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Flyway migration strategy that cleans the schema before migrating.
 * Usage:
 *   - Enable only in development by activating the "dev" profile via application.properties:
 *       spring.profiles.active=dev
 *   - Or programmatically in main application:
 *       SpringApplication app = new SpringApplication(SpringBootMailingApplication.class);
 *       app.setAdditionalProfiles("dev");
 *       app.run(args);
 *
 * WARNING: This will delete all data! Use only in dev.
 */

@Configuration
@Profile("dev") // only for development
public class FlywayConfig {
    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();    // Clean database schema before migration
            flyway.migrate();  // // Apply all migrations from baseline: V1, V2 etc.
        };
    }
}
