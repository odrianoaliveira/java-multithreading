package com.adriano.user.lookup.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var method = exchange.getRequestMethod();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String response;
            if ("GET".equalsIgnoreCase(method)) {
                response = "[{\"id\":1,\"username\":\"john\"}]";
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else if ("POST".equalsIgnoreCase(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                response = "{\"status\":\"User created\",\"data\":" + body + "}";
                exchange.sendResponseHeaders(201, response.getBytes().length);
            } else {
                response = "{\"error\":\"Method not allowed\"}";
                exchange.sendResponseHeaders(405, response.getBytes().length);
            }
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
