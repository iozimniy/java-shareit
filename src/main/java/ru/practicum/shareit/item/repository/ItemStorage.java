package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Optional<Item> getItemByOwner(Long itemId, Long userId);

    Item update(Item item);

    Optional<Item> getById(Long itemId);

    Collection<Item> getUserItems(Long userId);

    Collection<Item> getByText(String text);
}
