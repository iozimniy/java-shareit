package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Filter;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingDto, Long userId) throws ValidationException, NotFoundException;

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) throws NotFoundException, ValidationException;

    BookingDto getBooking(Long userId, Long bookingId) throws NotFoundException, ValidationException;

    Collection<BookingDto> getAllBookings(Long userId, Optional<Filter> filter) throws NotFoundException;

    Collection<BookingDto> getAllOwnerBookings(Long userId, Optional<Filter> filter) throws NotFoundException;
}
