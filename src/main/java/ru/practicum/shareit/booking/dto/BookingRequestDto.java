package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
