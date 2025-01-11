package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "available", source = "item.isAvailable")
    ItemDto toItemDto(Item item);

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "isAvailable", source = "itemDto.available")
    Item toItem (ItemDto itemDto, User user);
}
