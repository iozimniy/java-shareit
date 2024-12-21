package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {
    Long id;
    String name;
    @Email
    String email;
}
