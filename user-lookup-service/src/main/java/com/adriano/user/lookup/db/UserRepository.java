package com.adriano.user.lookup.db;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
    CompletableFuture<Optional<JdbcUserRepository.User>> findById(long id);
    CompletableFuture<JdbcUserRepository.User> save(JdbcUserRepository.User User);
}