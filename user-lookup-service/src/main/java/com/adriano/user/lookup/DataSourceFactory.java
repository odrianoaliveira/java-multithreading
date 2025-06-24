package com.adriano.user.lookup;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    private static final DataSourceFactory INSTANCE = new DataSourceFactory();
    private DataSource dataSource;

    public static DataSourceFactory getInstance() {
        return INSTANCE;
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = create();
        }

        return dataSource;
    }

    private DataSourceFactory() {
    }

    private DataSource create() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(10_000);
        dataSource = new HikariDataSource(config);
        return dataSource;
    }
}
