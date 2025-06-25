package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        if (user == null) return null;
        return users.put(user.getId(), user);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (user == null) return null;
        if (!users.containsKey(user.getId())) return null;
        return users.replace(user.getId(), user);
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }
}
