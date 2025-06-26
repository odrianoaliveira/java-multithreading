package com.adriano.user.lookup.db;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcUserRepositoryTest {

    private static JdbcDataSource dataSource;
    private JdbcUserRepository repository;

    @BeforeAll
    static void setupDatabase() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

    }

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        repository = new JdbcUserRepository(dataSource, executor);
        var result = repository.createTable().get();
        assertTrue(result);
    }

    @Test
    void testSaveAndFindUser() throws ExecutionException, InterruptedException {
        JdbcUserRepository.User user = new JdbcUserRepository.User(
                null,
                "testuser",
                "test@example.com",
                Instant.now()
        );

        var result = repository.save(user);
        assertTrue(result.get().id() > 0);

        var loaded = repository.findById(result.get().id());
        assertTrue(loaded.get().isPresent());
        assertEquals("testuser", loaded.get().get().username());
        assertEquals("test@example.com", loaded.get().get().email());
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }
}