package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(User newUser) throws ConflictException, ValidationException {
        validateEmail(newUser);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по id " + id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long id, User user) throws ConflictException, ValidationException, NotFoundException {
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
        validateId(id);
        userRepository.deleteById(id);
    }

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
