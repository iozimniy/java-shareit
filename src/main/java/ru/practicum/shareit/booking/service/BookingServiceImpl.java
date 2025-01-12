package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Filter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.filter.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    @Autowired(required = false)
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingDto, Long userId) throws ValidationException, NotFoundException {
        log.debug("Получен запрос от пользователя с id {} на создание бронирования {}", userId, bookingDto);
        var booker = userService.getUserForBooking(userId);
        var item = itemService.getItemForBooking(bookingDto.getItemId());
        validateBooking(bookingDto);
        Booking booking = bookingMapper.fromRequestToBooking(bookingDto,
                booker,
                item);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) throws NotFoundException,
            ValidationException {
        log.debug("Получен запрос от пользователя с id {} на изменение cтатуса бронирования {} на {}", userId,
                bookingId, approved);
        Booking booking = validateAndGetBooking(userId, bookingId);

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.toDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) throws NotFoundException, ValidationException {
        log.debug("Получен запрос от пользователя с id {} на просмотр бронирования с id {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено по id " + bookingId));

        if (booking.getBooker().getId().equals(userId)
                || itemRepository.existsByIdAndOwnerId(booking.getItem().getId(), userId)) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new ValidationException("Пользователь c id " + userId + " не имеет прав для просмотра бронирования");
        }
    }

    @Override
    public Collection<BookingDto> getAllBookings(Long userId, Optional<Filter> filter) throws NotFoundException {
        log.debug("Получен запрос от пользователя с id {} на просмотр бронирований с фильтром {}", userId, filter);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден по id " + userId);
        }


        if (filter.isEmpty()) {
            return bookingRepository.findAllByBookerId(userId).stream()
                    .map(booking -> bookingMapper.toDTO(booking))
                    .collect(Collectors.toList());
        }

        Map<Filter, BookingStateFetchStrategy> filterStrategy = new HashMap<>();
        filterStrategy.put(Filter.CURRENT, new CurrentStateStrategy(bookingRepository, bookingMapper));
        filterStrategy.put(Filter.PAST, new PastStateStrategy(bookingRepository, bookingMapper));
        filterStrategy.put(Filter.FUTURE, new FutureStateStrategy(bookingRepository, bookingMapper));
        filterStrategy.put(Filter.WAITING, new WaitingStateStrategy(bookingRepository, bookingMapper));
        filterStrategy.put(Filter.REJECTED, new RejectedStateStrategy(bookingRepository, bookingMapper));

        return filterStrategy.get(filter).getBookings(userId);
    }

    @Override
    public Collection<BookingDto> getAllOwnerBookings(Long userId, Optional<Filter> filter) throws NotFoundException {
        log.debug("Получен запрос от пользователя с id {} на просмотр бронирований его вещей с фильтром {}", userId,
                filter);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (!itemRepository.existsByOwnerId(userId)) {
            throw new NotFoundException("У пользователя с id " + userId + "нет вещей");
        }

        if (filter.isEmpty()) {
            return bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(userId).stream()
                    .map(booking -> bookingMapper.toDTO(booking))
                    .collect(Collectors.toList());
        }

        switch (filter.get()) {
            case CURRENT -> {
                return bookingRepository.findAllCurrentBookingsByOwnerId(userId,
                                LocalDateTime.now(), Status.APPROVED).stream()
                        .map(booking -> bookingMapper.toDTO(booking))
                        .collect(Collectors.toList());
            }
            case PAST -> {
                return bookingRepository.findAllFinishedBookingByOwnerId(userId,
                                LocalDateTime.now()).stream()
                        .map(booking -> bookingMapper.toDTO(booking))
                        .collect(Collectors.toList());
            }
            case FUTURE -> {
                return bookingRepository.findAllFutureBookingsByOwnerId(
                        userId, LocalDateTime.now(), Status.APPROVED
                ).stream().map(booking -> bookingMapper.toDTO(booking)).collect(Collectors.toList());
            }
            case WAITING -> {
                return bookingRepository.findAllBookingByOwnerAndStatus(userId, Status.WAITING)
                        .stream().map(booking -> bookingMapper.toDTO(booking)).collect(Collectors.toList());
            }
            case REJECTED -> {
                return bookingRepository.findAllBookingByOwnerAndStatus(userId, Status.REJECTED)
                        .stream().map(booking -> bookingMapper.toDTO(booking)).collect(Collectors.toList());
            }
        }

        return null;
    }


    //вспомогательные методы

    private void validateBooking(BookingRequestDto bookingDto) throws ValidationException {
        if ((bookingDto.getStart().isAfter(bookingDto.getEnd())) ||
                bookingDto.getStart() == bookingDto.getEnd()) {
            throw new ValidationException("Некорретный период бронирования");
        }
    }

    private Booking validateAndGetBooking(Long userId, Long bookingId) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено по id " + bookingId));
        if (itemRepository.existsByIdAndOwnerId(booking.getItem().getId(), userId)) {
            return booking;
        } else {
            throw new ValidationException("У пользователя c id " + userId + " нет вещи с id " + booking.getItem().getId());
        }
    }
}
