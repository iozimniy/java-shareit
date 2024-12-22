package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserStorage userStorage;

    @Override
    public UserDto create(User newUser) {
        validateEmail(newUser);
        return UserMapper.toUserDto(userStorage.create(newUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, User user) {
        Optional<User> oldUser = userStorage.getUserById(id);
        if (oldUser.isPresent()) {
            user = updateUser(oldUser.get(), user);
            return UserMapper.toUserDto(userStorage.update(user));
        } else {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    private User updateUser(User oldUser, User newUser) {
        newUser.setId(oldUser.getId());

        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        } else if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }

        if (!newUser.getEmail().equals(oldUser.getEmail())) {
            validateEmail(newUser);
        }

        return newUser;
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        userStorage.delete(id);
    }

    @Override
    public void validateId(Long id) {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    private void validateEmail(User newUser) {
        if (newUser.getEmail() == null) {
            throw new ValidationException("Не предоставлен email для создания пользователя");
        }

        userStorage.getAllUsers().stream().forEach(user -> {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует.");
            }
        });
    }
}
