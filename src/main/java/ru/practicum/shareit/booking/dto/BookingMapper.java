package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "start", source = "booking.startDate")
    @Mapping(target = "end", source = "booking.endDate")
    BookingDto toDTO(Booking booking);

    @Mapping(target = "booker", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "bookingRequestDto.start")
    @Mapping(target = "endDate", source = "bookingRequestDto.end")
    @Mapping(target = "item", source = "bookingItem")
    Booking fromRequestToBooking(BookingRequestDto bookingRequestDto, User user, Item bookingItem);
}
