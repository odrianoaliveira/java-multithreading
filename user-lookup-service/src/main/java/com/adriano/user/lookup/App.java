package com.adriano.user.lookup;

import com.adriano.user.lookup.api.ApiServer;
import com.adriano.user.lookup.api.UserHandlers;
import com.adriano.user.lookup.db.DataSourceFactory;
import com.adriano.user.lookup.db.JdbcUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

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
        var factory = DataSourceFactory.getInstance();
        var dataSource = factory.getDataSource();
        var dbPool = Executors.newCachedThreadPool();
        var repository = new JdbcUserRepository(dataSource, dbPool);
        migrateDB(repository).join();

        var handlers = new UserHandlers(new UserManagement(repository));
        try {
            var apiServer = new ApiServer(handlers);
            apiServer.start(9000);
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
