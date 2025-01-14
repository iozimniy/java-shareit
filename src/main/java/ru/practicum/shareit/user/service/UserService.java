package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto create(User user) throws ConflictException, ValidationException;

    UserDto getUserById(Long id) throws NotFoundException;

    Collection<UserDto> getAllUsers();

    UserDto update(Long id, User user) throws ConflictException, ValidationException, NotFoundException;

    void delete(Long id) throws NotFoundException;

    void validateId(Long id) throws NotFoundException;

    User getUserForBooking(Long userId) throws NotFoundException;
}
