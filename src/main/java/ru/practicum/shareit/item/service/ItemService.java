package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDto update(Long itemId, Long userId, ItemDto itemDto) throws NotFoundException, ValidationException;

    ItemDTOWithBookings getItem(Long itemId) throws NotFoundException;

    Collection<ItemDTOWithBookings> getUserItems(Long userId) throws NotFoundException;

    Item getItemForBooking(Long itemId) throws NotFoundException, ValidationException;

    Collection<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) throws NotFoundException, ValidationException;
}
