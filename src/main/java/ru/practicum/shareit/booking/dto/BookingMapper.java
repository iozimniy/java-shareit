package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto toDTO(Booking booking);
    @Mapping(target = "status", source = "newStatus")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "id", source = "bookingDto.id")
    Booking toBooking(BookingDto bookingDto, User user, Status newStatus);
}
