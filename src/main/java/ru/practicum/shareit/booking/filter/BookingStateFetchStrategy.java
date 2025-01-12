package ru.practicum.shareit.booking.filter;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingStateFetchStrategy {
    List<BookingDto> getBookings(Long userId);
}
