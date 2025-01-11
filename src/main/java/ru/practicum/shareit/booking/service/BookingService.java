package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Filter;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    BookingDto create(Booking booking) throws ValidationException;

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) throws NotFoundException;

    BookingDto getBooking(Long userId, Long bookingId) throws NotFoundException;

    Collection<BookingDto> getAllBookings(Long userId, Optional<Filter> filter) throws NotFoundException;

    Collection<BookingDto> getAllOwnerBookings(Long userId, Optional<Filter> filter) throws NotFoundException;
}