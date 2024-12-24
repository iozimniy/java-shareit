package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class ItemDto {
    Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    String description;
    Boolean available;
}
