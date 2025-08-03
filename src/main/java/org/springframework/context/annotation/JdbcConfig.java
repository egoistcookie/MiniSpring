package org.springframework.context.annotation;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.config.JdbcProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {
    @Bean
    public DataSource dataSource(JdbcProperties jdbcProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcProperties.getUrl());
        config.setUsername(jdbcProperties.getUsername());
        config.setPassword(jdbcProperties.getPassword());
        return new HikariDataSource(config);
    }

    @Bean
    public PlatformTransactionManager transactionManager(HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource); // 注入 DataSource
    }
}