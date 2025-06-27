package com.adriano.user.lookup;

import com.adriano.user.lookup.db.JdbcUserRepository;

import java.util.Optional;

public class UserManagement {

    private final JdbcUserRepository repository;

    public UserManagement(JdbcUserRepository repository) {
        this.repository = repository;
    }

    public Optional<JdbcUserRepository.User> find(long id) {
        var future = repository.findById(id);
        return future.join();
    }

    public JdbcUserRepository.User created(JdbcUserRepository.User user) {
        var future = repository.save(user);
        return future.join();
    }
}
