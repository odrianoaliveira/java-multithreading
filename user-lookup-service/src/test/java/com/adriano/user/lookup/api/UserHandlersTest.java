package com.adriano.user.lookup.api;

import com.adriano.user.lookup.UserManagement;
import com.adriano.user.lookup.db.JdbcUserRepository;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ExtendWith(VertxExtension.class)
class UserHandlersTest {

    private int actualPort = 8888;
    private UserManagement userManagement;
    private WebClient client;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext testContext) {
        this.userManagement = Mockito.mock(UserManagement.class);

        var handlers = new UserHandlers(userManagement);
        var router = Router.router(vertx);
        router.get("/users/:id").handler(handlers::handleGetUserById);
        router.get("/users").handler(handlers::handleGetAllUsers);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(0)
                .onSuccess(server -> {
                    client = WebClient.create(vertx);
                    testContext.completeNow();
                    actualPort = server.actualPort();
                })
                .onFailure(testContext::failNow);
    }

    @Test
    void testGetUserById_success(VertxTestContext testContext) {
        var user = new JdbcUserRepository.User(
                1L,
                "john",
                "john@host.com",
                Instant.now().minus(1, ChronoUnit.DAYS)
        );
        Mockito.when(userManagement.find(1L)).thenReturn(Optional.of(user));

        client.get(actualPort, "localhost", "/users/1")
                .send()
                .onSuccess(resp -> {
                    testContext.verify(() -> {
                        Assertions.assertEquals(200, resp.statusCode());
                        Assertions.assertEquals(resp.bodyAsString(), Json.encode(user));
                    });
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }

    @Test
    void testGetUserById_invalidId(VertxTestContext testContext) {
        client.get(actualPort, "localhost", "/users/abc")
                .send()
                .onSuccess(resp -> {
                    testContext.verify(() -> {
                        Assertions.assertEquals(400, resp.statusCode());
                        Assertions.assertTrue(resp.bodyAsString().contains("invalid user id"));
                    });
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }

    @Test
    void testGetAllUsers(VertxTestContext testContext) {
        client.get(actualPort, "localhost", "/users")
                .send()
                .onSuccess(resp -> {
                    testContext.verify(() -> {
                        Assertions.assertEquals(200, resp.statusCode());
                        Assertions.assertTrue(resp.bodyAsString().contains("john"));
                    });
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }
}