package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class BookingDto {
    Long id;
    @NotNull
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    LocalDateTime start;
    @NotNull
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    LocalDateTime end;
    Item item;
    User booker;
    Status status;
}
