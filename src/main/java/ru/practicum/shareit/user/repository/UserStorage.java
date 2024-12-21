package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    Collection<User> getAllUsers();

    boolean contains(Long id);

    User getUserById(Long id);

    User update(Long id, User user);

    void delete(Long id);
}
