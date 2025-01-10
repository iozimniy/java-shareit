package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    Map<Long, Item> items = new HashMap<>();
    Long idCounter = 0L;


    @Override
    public Item create(Item item) {
        item.setId(idCounter++);
        items.put(item.getId(), item);
        log.info("Добавлена вещь: {}", item);
        return item;
    }

    @Override
    public Optional<Item> getItemByOwner(Long itemId, Long userId) {
        return items.values().stream()
                .filter(item -> item.getId().equals(itemId) && item.getOwnerId().equals(userId))
                .findAny();
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь {}", item);
        return item;
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return items.values().stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny();
    }

    @Override
    public Collection<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getByText(String text) {
        return items.values().stream()
                .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text)
                        || StringUtils.containsIgnoreCase(item.getDescription(), text))
                        && item.getIsAvailable()
                )
                .collect(Collectors.toList());
    }
}
