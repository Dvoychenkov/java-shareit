package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.*;

public class UserRepositoryInMemory {
    private long userIdCnt = 0;
    private final Map<Long, User> users = new HashMap<>();

    public User add(User user) {
        if (user == null) {
            return null;
        }
        user.setId(++userIdCnt);
        users.put(user.getId(), user);
        return user;
    }

    public User save(User user) {
        if (user == null) {
            return null;
        }
        if (!users.containsKey(user.getId())) {
            return null;
        }
        users.replace(user.getId(), user);
        return user;
    }

    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(users.values()));
    }

    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void remove(Long id) {
        users.remove(id);
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(Objects::nonNull)
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}
