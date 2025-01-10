package ru.practicum.shareit.user.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody User user) throws ValidationException, ConflictException {
        log.debug("Получен запроса на создание пользователя: {}", user);
        return userService.create(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) throws NotFoundException {
        log.debug("Получен запрос на получение информации о пользователе с id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.debug("Получен запрос на получение информации обо всех пользователях");
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody User user, @PathVariable Long id) throws ValidationException, ConflictException, NotFoundException {
        log.debug("Получен запрос на изменение пользователя {} с id {}", user, id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws NotFoundException {
        log.debug("Получен запрос на удаление пользователя с id {}", id);
        userService.delete(id);
    }
}
