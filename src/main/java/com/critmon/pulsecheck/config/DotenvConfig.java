package com.critmon.pulsecheck.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class DotenvConfig {

    public static void loadEnvironmentVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("C:\\Users\\LENOVO\\Documents\\Pulse-Check-API")
                    .load();

            String dbUrl = dotenv.get("DATABASE_URL");
            String dbUsername = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");
            String serverPort = dotenv.get("SERVER_PORT");
            String contextPath = dotenv.get("CONTEXT_PATH");
            String logLevel = dotenv.get("LOG_LEVEL");
            String cacheMaxSize = dotenv.get("CACHE_MAX_SIZE");
            String cacheExpireMinutes = dotenv.get("CACHE_EXPIRE_MINUTES");
            String cacheLogLevel = dotenv.get("CACHE_LOG_LEVEL");
            String smtpHost = dotenv.get("SMTP_HOST");
            String smtpPort = dotenv.get("SMTP_PORT");
            String smtpUsername = dotenv.get("SMTP_USERNAME");
            String smtpPassword = dotenv.get("SMTP_PASSWORD");

        } catch (DotenvException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
            System.err.println("Please ensure .env file exists in the project root directory");
            throw new RuntimeException("Failed to load environment variables", e);
        }
    }
}
