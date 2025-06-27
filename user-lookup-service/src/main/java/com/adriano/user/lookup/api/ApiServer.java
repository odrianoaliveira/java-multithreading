package com.adriano.user.lookup.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApiServer {
    private static final Logger logger = LoggerFactory.getLogger(ApiServer.class);
    private final UserHandlers userHandlers;

    public ApiServer(UserHandlers userHandlers) {
        this.userHandlers = userHandlers;
    }

    public void start(int port) throws IOException {
        logger.info("Starting server on port {}", port);
        var vertx = Vertx.vertx();
        var router = Router.router(vertx);

        router.get("/user/:id").handler(userHandlers::handleGetUserById);
        router.get("/user").handler(userHandlers::handleGetAllUsers);
        router.post("/user").handler(userHandlers::handleCreateUser);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port);

        logger.info("Server started on port {}}", port);
    }
}
