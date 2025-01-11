package ru.practicum.shareit.booking.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Filter;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Collection;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingRequestDto bookingDto) throws ValidationException, NotFoundException {
      log.debug("Получен запрос на создание бронирования: {}", bookingDto);
      return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) throws NotFoundException, ValidationException {
        log.debug("Получен запрос на изменение бронирования от пользователя с id {} для вещи с id {} и статусом {}",
                userId, bookingId, approved);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) throws NotFoundException, ValidationException {
        log.debug("Получен запрос на просмотр бронирования с id {} от пользователя с id {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false) Optional<Filter> filter) throws NotFoundException {
        return bookingService.getAllBookings(userId, filter);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false) Optional<Filter> filter) throws NotFoundException {
        log.debug("Получен запрос на просмотр всех бронирований владельца с id {} с фильтром {}", userId, filter);
        return bookingService.getAllOwnerBookings(userId, filter);
    }
}
