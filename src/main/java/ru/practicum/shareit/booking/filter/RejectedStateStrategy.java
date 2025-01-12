package ru.practicum.shareit.booking.filter;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RejectedStateStrategy implements BookingStateFetchStrategy {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    @Override
    public List<BookingDto> getBookings(Long userId) {
        return bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED)
                .stream().map(booking -> bookingMapper.toDTO(booking))
                .collect(Collectors.toList());
    }
}
