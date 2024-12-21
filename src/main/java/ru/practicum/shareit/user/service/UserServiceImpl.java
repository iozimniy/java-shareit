package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(User newUser) {
        validateEmail(newUser);
        return UserMapper.toUserDto(userStorage.create(newUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        validateId(id);
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public UserDto update(Long id, User user) {
        validateId(id);
        user = updateUser(user, id);
        return UserMapper.toUserDto(userStorage.update(id, user));
    }

    private User updateUser(User newUser, Long id) {
        User oldUser = userStorage.getUserById(id);

        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        } else if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        } else if (!newUser.getEmail().equals(oldUser.getEmail())) {
            validateEmail(newUser);
        }

        return newUser;
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        userStorage.delete(id);
    }

    private void validateId(Long id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    private void validateEmail(User newUser) {
        if (newUser.getEmail() == null) {
            throw new ValidationException("Не предоставлен email создания пользователя");
        }

        userStorage.getAllUsers().stream().forEach(user -> {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
        });
    }
}
