package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatusOrderByStart(Long userId, Status status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStart(Long userId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByBookerIdAndStatusOrBookerIdAndStatusOrderByStart(Long userId1, Status status1, Long userId2, Status status2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStart(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartAfterOrderByStart(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdOrderByStart(Long userId);

    List<Booking> findByItem_OwnerIdAndStatusOrderByStart(Long userId, Status status);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStart(Long userId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatusOrderByStart(Long userId1, Status status1, Long userId2, Status status2);

    List<Booking> findByItem_OwnerIdAndEndBeforeOrderByStart(Long userId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndStartAfterOrderByStart(Long userId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdOrderByStart(Long userId);

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartDesc(Long itemId, Status status, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(Long itemId, Status status, LocalDateTime time);
}