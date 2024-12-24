package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class User {
    Long id;
    @NotBlank
    String name;
    @Email
    String email;
}
