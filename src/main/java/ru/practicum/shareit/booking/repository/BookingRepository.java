package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findAllByBookerId(Long bookerId);

    @Query("select b from Booking as b where b.id = ?1 and b.status = ?2 order by b.startDate desc")
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status);

    @Query("select b from Booking as b where b.id = ?1 and b.endDate < ?2 order by b.endDate desc")
    List<Booking> findAllByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime time);

    @Query("select b from Booking as b where b.id = ?1 and b.endDate > ?2 and b.status = ?3 order by b.endDate desc")
    List<Booking> findAllFinishedBookingsById(Long bookerId,
                                              LocalDateTime time,
                                              Status status);

    @Query("select b from Booking as b where b.id = ?1 and b.startDate > ?2 and b.status = ?3 " +
            "order by b.startDate desc")
    List<Booking> findAllFutureBookingsById(Long bookerId,
                                            LocalDateTime time,
                                            Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    @Query("select b from Booking as b join b.item as it join it.owner as ow where ow.id = ?1 and b.endDate > ?2 " +
            "and b.status = ?3 order by b.endDate desc")
    List<Booking> findAllCurrentBookingsByOwnerId(Long ownerId,
                                                  LocalDateTime time,
                                                  Status status);

    @Query("select b from Booking as b join b.item as it join it.owner as ow where ow.id = ?1 and b.endDate < ?2 " +
            "order by b.endDate desc")
    List<Booking> findAllFinishedBookingByOwnerId(Long ownerId, LocalDateTime time);

    @Query("select b from Booking as b join b.item as it join it.owner as ow where ow.id = ?1 and b.startDate > ?2 " +
            "order by b.startDate desc")
    List<Booking> findAllFutureBookingsByOwnerId(Long ownerId,
                                                 LocalDateTime time, Status status);

    @Query("select b from Booking as b join b.item as it join it.owner as ow where ow.id = ?1 and b.status = ?2 " +
            "order by b.endDate desc")
    List<Booking> findAllBookingByOwnerAndStatus(Long ownerId, Status status);

    List<Booking> findAllByItemId(Long itemId);
}
