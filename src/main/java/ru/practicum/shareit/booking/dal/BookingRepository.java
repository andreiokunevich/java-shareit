package ru.practicum.shareit.booking.dal;

import org.dom4j.datatype.DatatypeElementFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime time1, LocalDateTime time2, Sort sort);

    List<Booking> findByBookerIdAndStatusOrBookerIdAndStatus(Long userId1, Status status1, Long userId2, Status status2, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerId(Long userId, Sort sort);

    List<Booking> findByItem_OwnerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime time1, LocalDateTime time2, Sort sort);

    List<Booking> findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatus(Long userId1, Status status1, Long userId2, Status status2, Sort sort);

    List<Booking> findByItem_OwnerIdAndEndBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByItem_OwnerId(Long userId, Sort sort);

    Optional<Booking> findByIdAndItem_OwnerId(Long bookingId, Long userId);

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfter(Long itemId, Status status, LocalDateTime time, Sort sort);
}