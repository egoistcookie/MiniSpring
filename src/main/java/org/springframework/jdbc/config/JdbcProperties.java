package org.springframework.jdbc.config;

import java.io.InputStream;
import java.util.Properties;

public class JdbcProperties {
    private final Properties properties;

    public JdbcProperties() {
        this.properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("jdbc.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load jdbc.properties", e);
        }
    }

    public String getUrl() {
        return properties.getProperty("jdbc.url");
    }

    public String getUsername() {
        return properties.getProperty("jdbc.username");
    }

    public String getPassword() {
        return properties.getProperty("jdbc.password");
    }
}