package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@AllArgsConstructor
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
