package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId);
    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, Status status);
    List<Booking> findAllByBookerIdAndEndDateIsBeforeOrderByEndDateDesc(Long bookerId, LocalDateTime time);
    List<Booking> findAllByBookerIdAndEndDateIsAfterAndStatusOrderByEndDateDesc(Long bookerId,
                                                                                LocalDateTime time,
                                                                                Status status);
    List<Booking> findAllByBookerIdAndStartDateIsAfterAndStatusOrderByEndDateDesc(Long bookerId,
                                                                                LocalDateTime time,
                                                                                Status status);

    @Query("select b from Booking as b join Item it where it.ownerId = ?1")
    List<Booking> findAllByOwner(Long ownerId);

    @Query("select b from Booking as b join Item it where it.ownerId = ?1 and b.endDate > ?2")
    List<Booking> findAllByOwnerIdAndEndDateIsAfterOrderByEndDateDesc(Long ownerId, LocalDateTime time);
}