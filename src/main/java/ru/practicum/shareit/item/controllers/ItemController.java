package ru.practicum.shareit.item.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на создание вещи: {} от пользователя с id {}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable("id") Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на изменение вещи {} c id {} от пользователя с id {}", itemDto, itemId, userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Получен запрос на просмотр вещи с id {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на просмотр всех вещей пользователя с id {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Получен запрс на поиск по тексту: {}", text);
        return itemService.search(text);
    }
}