## 📋 Challenge: Asynchronous User Lookup Service

### 1. Background

You’re building a microservice that exposes user data to other services. For high throughput you must avoid tying up HTTP request-handling threads on blocking JDBC calls. Instead, you’ll offload JDBC work to a dedicated thread pool and expose a fully asynchronous API.

### 2. Requirements

* **Tech stack**: Java 21+, plain JDBC, HikariCP (or similar), no Spring.

* **Database schema**: a single table

  ```sql
  CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
  );
  ```

* **Connection pool**: configure HikariCP with sensible defaults (maxPoolSize=10, connectionTimeout=-).

* **API surface**: implement

  ```java
  public interface UserRepository {
    /**  
     * Returns a future that completes with the user,  
     * or completes exceptionally if not found or on error/timeout.  
     */
    CompletableFuture<User> findById(long id);
  }
  ```

* **Threading**:
    * Use a bounded `ExecutorService` (e.g. `ThreadPoolExecutor`) for JDBC calls.
    * Do *not* call blocking I/O on the common ForkJoinPool.

* **Timeouts & Cancellation**:
    * If a query takes longer than 500 ms, the `CompletableFuture` should complete exceptionally with a `TimeoutException`.
    * Ensure you properly cancel the JDBC work and don’t leak threads or connections.

* **Error handling**:
    * SQLExceptions should be wrapped in a custom `DataAccessException`.
    * Ensure that connections, statements, and result sets are always closed (e.g. try-with-resources).

* **Metrics (bonus)**:
    * Count successful vs. failed calls.
    * Track average query latency.

### 3. Starter Skeleton

```java
public class HikariDataSourceFactory {
    public static DataSource create() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("pass");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(10_000);
        return new HikariDataSource(config);
    }
}

public class User {
    private final long id;
    private final String username;
    private final String email;
    private final Instant createdAt;
    // constructor + getters
}

public interface UserRepository {
    CompletableFuture<User> findById(long id);
}

// Your implementation goes here:
public class JdbcUserRepository implements UserRepository {
    private final DataSource ds;
    private final ExecutorService jdbcExecutor;
    private final long timeoutMillis = 500;

    public JdbcUserRepository(DataSource ds, ExecutorService jdbcExecutor) {
        this.ds = ds;
        this.jdbcExecutor = jdbcExecutor;
    }

    @Override
    public CompletableFuture<User> findById(long id) {
        // TODO: implement using CompletableFuture.supplyAsync,
        // apply timeout, map exceptions, close resources.
    }
}
```

### 4. Tasks

1. **Implement** `JdbcUserRepository.findById(...)` to:

    * Offload the `Connection → PreparedStatement → ResultSet` work to your `jdbcExecutor`.
    * Apply a 500 ms timeout (hint: `orTimeout` or `completeOnTimeout` on `CompletableFuture`).
    * Map `ResultSet` into your `User` object, throw if not found.
    * Wrap all SQLExceptions into `DataAccessException`.
    * Ensure *all* JDBC resources are closed, even on timeout.

2. **Write unit tests** (using JUnit):

    * Happy path: user exists → future completes with correct `User`.
    * Not found: future completes exceptionally with `NoSuchElementException`.
    * Slow query (simulate via `pg_sleep` or proxy) → future completes exceptionally with `TimeoutException`.
    * Resource leak test: verify pool has no leaked connections after failures/timeouts.

3. **(Bonus)** Add simple metrics:

    * Use Micrometer or a thread-safe counter to count successes/failures and measure latencies.

### 5. Evaluation Criteria

* **Correctness**: does it meet the API contract?
* **Resource safety**: no connection or thread leaks.
* **Robustness**: proper exception mapping and timeouts.
* **Code quality**: clear abstractions, no blocking in the wrong thread pool.
* **Tests**: adequate coverage of success, error, timeout.

