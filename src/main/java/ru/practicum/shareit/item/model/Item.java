package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    ItemRequest request;
}
