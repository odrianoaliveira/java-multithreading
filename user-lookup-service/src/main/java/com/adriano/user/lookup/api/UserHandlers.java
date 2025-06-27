package com.adriano.user.lookup.api;

import com.adriano.user.lookup.UserManagement;
import com.adriano.user.lookup.db.JdbcUserRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;


public class UserHandlers {
    private final UserManagement userManagement;

    public UserHandlers(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    void handleGetUserById(RoutingContext ctx) {
        var id = ctx.pathParam("id");
        var response = ctx.response();
        response.putHeader("content-type", "application/json");

        parseId(id, response)
                .map(i -> {
                    var future = userManagement.find(i);
                    var user = future.get();
                    var payload = Json.encode(user);
                    response.end(payload);
                    return null;
                }).orElseGet(() -> {
                    var error = new ErrorDetails("invalid user id");
                    var payload = Json.encode(error);
                    response.setStatusCode(400).end(payload);
                    return null;
                });
    }

    Optional<Long> parseId(String id, HttpServerResponse response) {
        try {
            return Optional.of(Long.parseLong(id));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    void handleGetAllUsers(RoutingContext ctx) {
        var response = ctx.response();
        response.putHeader("content-type", "application/json");
        // Replace with actual user list
        response.end("[{\"id\":1,\"username\":\"john\"}]");
    }

    void handleCreateUser(RoutingContext ctx) {
        var response = ctx.response();
        response.putHeader("content-type", "application/json");
        ctx.request().bodyHandler(body -> {
            // Replace with actual user creation logic
            response.setStatusCode(201)
                    .end("{\"status\":\"User created\",\"data\":" + body.toString() + "}");
        });
    }
}

