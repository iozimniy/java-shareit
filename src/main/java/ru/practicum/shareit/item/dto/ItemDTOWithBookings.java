package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.List;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDTOWithBookings {
    Long id;
    String name;
    String description;
    Boolean available;
    LocalDateTime lastBookingEndDate;
    LocalDateTime nextBookingStartDate;
}
