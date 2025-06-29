package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User add(User user);

    User save(User user);

    Collection<User> findAll();

    Optional<User> find(Long id);

    void remove(Long id);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);
}