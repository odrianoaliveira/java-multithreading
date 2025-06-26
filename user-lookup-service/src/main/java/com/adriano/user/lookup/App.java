package com.adriano.user.lookup;

import com.adriano.user.lookup.db.DataSourceFactory;
import com.adriano.user.lookup.db.JdbcUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting App...");
        App.run();
        logger.info("App is shutdown.");
    }

    private static void run() {
        var dataSourceFactory = DataSourceFactory.getInstance();
        var ds = dataSourceFactory.getDataSource();

        var threadPool = Executors.newCachedThreadPool();
        var userRepository = new JdbcUserRepository(ds, threadPool);
        migrateDB(userRepository).join();
        threadPool.shutdown();
    }

    private static CompletableFuture<Void> migrateDB(JdbcUserRepository userRepository) {
        return userRepository
                .createTable()
                .thenAccept(succeed -> {
                    if (!succeed) {
                        logger.error("Error creating users table.");
                        return;
                    }
                    logger.info("Users table created successfully");
                });
    }
}
