package com.fakenews.detection.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Smart DataSource Configuration:
 * - If MySQL credentials are valid, connects directly to MySQL (fake_news_db).
 * - If password is still 'YOUR_PASSWORD' or MySQL access is denied, gracefully falls back
 *   to an embedded database (./data/fake_news_db) so the full stack web app works out of the box!
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/fake_news_db}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUser;

    @Value("${spring.datasource.password:YOUR_PASSWORD}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // If password is still placeholder 'YOUR_PASSWORD', fall back to embedded DB immediately
        if ("YOUR_PASSWORD".equalsIgnoreCase(mysqlPassword) || mysqlPassword.trim().isEmpty()) {
            log.warn("================================================================================");
            log.warn("[NOTICE] spring.datasource.password is still 'YOUR_PASSWORD' in application.properties.");
            log.warn("Starting with embedded database (./data/fake_news_db) so the full stack app works immediately!");
            log.warn("To connect to MySQL, replace YOUR_PASSWORD in application.properties with your MySQL root password.");
            log.warn("================================================================================");
            return createEmbeddedDataSource();
        }

        // Try connecting to MySQL with the configured credentials
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)) {
                log.info("Successfully connected to MySQL database at: {}", mysqlUrl);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(mysqlUrl);
            config.setUsername(mysqlUser);
            config.setPassword(mysqlPassword);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(10);
            return new HikariDataSource(config);

        } catch (Exception e) {
            log.warn("================================================================================");
            log.warn("[WARN] Could not connect to MySQL server. Reason: {}", e.getMessage());
            log.warn("Automatically falling back to embedded persistent database (./data/fake_news_db) so the app keeps running!");
            log.warn("================================================================================");
            return createEmbeddedDataSource();
        }
    }

    private DataSource createEmbeddedDataSource() {
        HikariConfig config = new HikariConfig();
        // File-based database that persists data in ./data/fake_news_db across restarts
        config.setJdbcUrl("jdbc:h2:file:./data/fake_news_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
