package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(User newUser) throws ConflictException, ValidationException {
        log.debug("Получен запрос на создание пользователя {}", newUser);
        validateEmail(newUser);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        log.debug("Получен запрос на просмотр пользователя с id {}", id);
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.debug("Получен запрос на просмотр всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long id, User user) throws ConflictException, ValidationException, NotFoundException {
        log.debug("Получен запрос на изменение пользователя с id {} с данными {}", id, user);
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + id));
        user = updateUser(oldUser, user);
        return UserMapper.toUserDto(userRepository.save(user));
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
    @Transactional
    public void delete(Long id) throws NotFoundException {
        log.debug("Получен запрос на удаление пользователя с id {}", id);
        validateId(id);
        userRepository.deleteById(id);
    }

    //вспомогательные методы

    @Override
    public void validateId(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден по id " + id);
        }
    }

    @Override
    public User getUserForBooking(Long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + userId));
    }

    private void validateEmail(User newUser) throws ValidationException, ConflictException {
        if (newUser.getEmail() == null) {
            throw new ValidationException("Не предоставлен email для создания пользователя");
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException("Этот email уже занят");
        }
    }
}
