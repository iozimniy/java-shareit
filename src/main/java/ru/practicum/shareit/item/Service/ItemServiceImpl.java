package ru.practicum.shareit.item.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    UserService userService;
    ItemStorage itemStorage;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validateUserId(userId);
        validateItemDtoForCreate(itemDto);
        Item item = ItemMapper.toItem(itemDto, userId);
        item = itemStorage.create(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        validateUserId(userId);
        validateItemDtoForUpdate(itemDto);
        Optional<Item> oldItem = itemStorage.getItemByOwner(itemId, userId);

        if (oldItem.isEmpty()) {
            throw new NotFoundException("У пользователя c id " + userId + " нет вещи с id " + itemId);
        }

        Item item = updateItem(oldItem.get(), ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(itemStorage.update(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Optional<Item> item = itemStorage.getById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new NotFoundException("Вещь не найдена по id");
        }
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        validateUserId(userId);
        return itemStorage.getUserItems(userId).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return itemStorage.getByText(text).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    private Item updateItem(Item oldItem, Item newItem) {
        newItem.setId(oldItem.getId());
        newItem.setRequest(oldItem.getRequest());

        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }

        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }

        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }

        return newItem;
    }

    private void validateUserId(Long userId) {
        userService.validateId(userId);
    }

    private void validateItemDtoForCreate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("У вещи должно быть название и описание.");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("Необходимо указать доступность вещи для бронирования");
        }
    }

    private void validateItemDtoForUpdate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isEmpty()) {
            throw new ValidationException("У вещи должно быть название и описанине.");
        }

        if (itemDto.getDescription() != null && itemDto.getDescription().isEmpty()) {
            throw new ValidationException("У вещи должно быть название и описанине.");
        }
    }
}
