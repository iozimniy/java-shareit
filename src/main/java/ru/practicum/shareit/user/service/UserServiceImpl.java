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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserStorage userStorage;

    @Override
    public UserDto create(User newUser) throws ConflictException, ValidationException {
        validateEmail(newUser);
        return UserMapper.toUserDto(userStorage.create(newUser));
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        return userStorage.getUserById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, User user) throws ConflictException, ValidationException, NotFoundException {
        User oldUser = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + id));
        user = updateUser(oldUser, user);
        return UserMapper.toUserDto(userStorage.update(user));
    }

    private User updateUser(User oldUser, User newUser) throws ConflictException, ValidationException {
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
    public void delete(Long id) throws NotFoundException {
        validateId(id);
        userStorage.delete(id);
    }

    @Override
    public void validateId(Long id) throws NotFoundException {
        if (!userStorage.contains(id)) {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    private void validateEmail(User newUser) throws ValidationException, ConflictException {
        if (newUser.getEmail() == null) {
            throw new ValidationException("Не предоставлен email для создания пользователя");
        }

        Collection<String> emails = userStorage.getAllUsers().stream()
                .map(user -> user.getEmail())
                .toList();

        if (emails.contains(newUser.getEmail())) {
            throw new ConflictException("Не предоставлен email для создания пользователя");
        }
    }
}
