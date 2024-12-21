package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> users = new HashMap<>();
    Long idCounter = 0L;

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public boolean contains(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

}
