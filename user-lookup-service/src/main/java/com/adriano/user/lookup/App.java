package com.adriano.user.lookup;

import com.adriano.user.lookup.db.DataSourceFactory;
import com.adriano.user.lookup.db.JdbcUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static com.adriano.user.lookup.api.Server.startServer;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting App...");
        try {
            App.run();
        } catch (Exception e) {
            logger.error("Failed to run the application", e);
        }
        logger.info("App is shutdown.");
    }

    private static void run() {
        var dataSourceFactory = DataSourceFactory.getInstance();
        var ds = dataSourceFactory.getDataSource();
        var dbThreadPool = Executors.newCachedThreadPool();
        var userRepository = new JdbcUserRepository(ds, dbThreadPool);
        migrateDB(userRepository).join();

        var requestThreadPool = Executors.newVirtualThreadPerTaskExecutor();
        try {
            startServer(requestThreadPool, 9000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CompletableFuture<Void> migrateDB(JdbcUserRepository userRepository) {
        return userRepository
                .createTable()
                .thenAccept(succeed -> {
                    if (!succeed) {
                        logger.error("Error creating users table.");
                        return;
                    }
                    logger.info("Users table created successfully.");
                });
    }
}
