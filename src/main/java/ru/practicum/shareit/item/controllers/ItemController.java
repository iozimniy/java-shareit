package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j()
public class ItemController {
    ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) throws ValidationException, NotFoundException {
        log.debug("Получен запрос на создание вещи: {} от пользователя с id {}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto itemDto) throws ValidationException, NotFoundException {
        log.debug("Получен запрос на изменение вещи {} c id {} от пользователя с id {}", itemDto, itemId, userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDTOWithBookings getItem(@PathVariable Long itemId) throws NotFoundException {
        log.debug("Получен запрос на просмотр вещи с id {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    @SneakyThrows
    public Collection<ItemDTOWithBookings> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на просмотр всех вещей пользователя с id {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.debug("Получен запрс на поиск по тексту: {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) throws NotFoundException, ValidationException {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
