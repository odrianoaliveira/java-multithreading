package com.adriano.user.lookup;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceFactoryTest {

    @Test
    void testSingletonInstance() {
        var instance1 = DataSourceFactory.getInstance();
        var instance2 = DataSourceFactory.getInstance();
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    void testGetDataSourceNotNull() {
        var dataSource = DataSourceFactory.getInstance().getDataSource();
        assertNotNull(dataSource, "DataSource should not be null");
    }

    @Test
    void testGetDataSourceIsHikariAndH2() {
        var dataSource = DataSourceFactory.getInstance().getDataSource();
        assertInstanceOf(HikariDataSource.class, dataSource, "Should be a HikariDataSource");
        var hikari = (HikariDataSource) dataSource;
        assertTrue(hikari.getJdbcUrl().startsWith("jdbc:h2:mem:"), "Should use H2 in-memory database");
    }

    @Test
    void testGetDataSourceReturnsSameInstance() {
        var factory = DataSourceFactory.getInstance();
        var ds1 = factory.getDataSource();
        var ds2 = factory.getDataSource();
        assertSame(ds1, ds2, "Should return the same DataSource instance");
    }
}