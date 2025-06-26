package com.adriano.user.lookup.db;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


public class JdbcUserRepository implements UserRepository {
    private final DataSource ds;
    private final ExecutorService jdbcExecutor;
    private final long timeoutMillis = 500;

    public record User(
            Long id,
            String username,
            String email,
            Instant createdAt
    ) {
    }

    public CompletableFuture<Boolean> createTable() {
        var query = """
                CREATE TABLE IF NOT EXISTS users (
                id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                username    VARCHAR(50)     NOT NULL,
                email       VARCHAR(100)    NOT NULL,
                created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                """.stripIndent();
        return CompletableFuture.supplyAsync(() -> {
            try (var statement = ds.getConnection().createStatement()) {
                statement.executeUpdate(query);
                return true;
            } catch (SQLException e) {
                System.err.println("Error trying to create the table." + e.getMessage());
                return false;
            }
        }, jdbcExecutor);
    }

    public JdbcUserRepository(DataSource ds, ExecutorService jdbcExecutor) {
        this.ds = ds;
        this.jdbcExecutor = jdbcExecutor;
    }

    @Override
    public CompletableFuture<Optional<User>> findById(long id) {
        var query = """
                SELECT id,
                        username,
                        email,
                        created_at
                   FROM users
                   WHERE id = ?;
                """.stripIndent();
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = ds.getConnection(); var stms = conn.prepareStatement(query)) {
                stms.setLong(1, id);
                try (var rs = stms.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new User(rs.getLong(1), rs.getString("username"), rs.getString("email"), rs.getTimestamp("created_at").toInstant()));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = ds.getConnection()) {
                if (user.id() == null) {
                    var insertQuery = """
                            INSERT INTO users (username, email, created_at)
                            VALUES (?, ?, ?)
                            """.stripIndent();
                    try (var stms = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        stms.setString(1, user.username());
                        stms.setString(2, user.email());
                        stms.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
                        stms.executeUpdate();
                        try (var rs = stms.getGeneratedKeys()) {
                            if (rs.next()) {
                                long generatedId = rs.getLong(1);
                                return new User(generatedId, user.username(), user.email(), Instant.now());
                            } else {
                                throw new SQLException("Failed to retrieve generated id.");
                            }
                        }
                    }
                } else {
                    var updateQuery = """
                            UPDATE users
                            SET username = ?, email = ?, created_at = ?
                            WHERE id = ?
                            """.stripIndent();
                    try (var stms = conn.prepareStatement(updateQuery)) {
                        stms.setString(1, user.username());
                        stms.setString(2, user.email());
                        stms.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
                        stms.setLong(4, user.id());
                        int updated = stms.executeUpdate();
                        if (updated == 0) {
                            throw new SQLException("No user found with id: " + user.id());
                        }
                        return new User(user.id(), user.username(), user.email(), Instant.now());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, jdbcExecutor);
    }
}
