package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Data
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    ItemRequest request;
}
