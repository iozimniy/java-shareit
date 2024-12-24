package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long id;
    String name;
    @Email
    String email;
}
