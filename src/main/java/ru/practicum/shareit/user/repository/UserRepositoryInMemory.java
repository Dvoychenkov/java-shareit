package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private long userIdCnt = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        if (user == null) {
            return null;
        }
        user.setId(++userIdCnt);
        users.put(user.getId(), user);
        return user;
    }

    @Override
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

    @Override
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(users.values()));
    }

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(Objects::nonNull)
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}
