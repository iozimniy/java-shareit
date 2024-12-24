package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    Collection<User> getAllUsers();

    boolean contains(Long id);

    Optional<User> getUserById(Long id);

    User update(User user);

    void delete(Long id);
}
