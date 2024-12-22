package ru.practicum.shareit.item.Service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getUserItems(Long userId);

    Collection<ItemDto> search(String text);
}
