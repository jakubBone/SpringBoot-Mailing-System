package com.jakubbone.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfigLoader {
    public static void loadEnvVariables() {
        // Load environment variables from the .env file using the Dotenv library
        Dotenv dotenv = Dotenv.configure().load();

        // Set the SPRING_DATASOURCE system properties using the value from .env
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

        // Set the JWT_SECRET system property using the value from .env
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    }
}
