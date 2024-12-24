package ru.practicum.shareit.item.Service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDto update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDto getItem(Long itemId) throws NotFoundException;

    Collection<ItemDto> getUserItems(Long userId) throws NotFoundException;

    Collection<ItemDto> search(String text);
}
