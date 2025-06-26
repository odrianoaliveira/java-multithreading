package com.adriano.user.lookup.api;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void startServer(Executor executor, int port) throws IOException {
        logger.info("Starting server on port {}", port);
        var server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/user", new Router.UserHandler());
        server.setExecutor(executor);
        server.start();
        logger.info("Server started on port {}}", port);
    }
}
