package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private  long userId = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User create(User user) {
        user.setId(++userId);
        userMap.put(user.getId(),user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        if (userMap.containsKey(id)) {
            return Optional.of(userMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void update(User newUser) {
        userMap.put(newUser.getId(),newUser);
    }

    @Override
    public void deleteUser(long id) {
        userMap.remove(id);
    }
}