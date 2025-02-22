package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.interfaces.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, NewBookingDto newBookingDto) {
        log.info("Попытка создания бронирования, пользователь: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с айди " + item.getId() + " недоступна!");
        }
        if (newBookingDto.getStart().isEqual(newBookingDto.getEnd()) || newBookingDto.getStart().isAfter(newBookingDto.getEnd())) {
            throw new ValidationException("Начальное время и время завершения бронирования не могут быть в одно время " +
                    "и(или) время завершения бронирования не может быть раньше начала!");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(user, item, newBookingDto)));
    }

    @Override
    @Transactional
    public BookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования от пользователя {}, айди бронирования {}", userId, bookingId);
        Booking booking = bookingRepository.findByIdAndItem_OwnerId(bookingId, userId)
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ValidationException("Запроса на бронирование не существует или вы не являетесь владельцем."));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Статус отличный от WAITING. Вещь не ожидает бронирования.");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Попытка поиска бронирования с айди {}", bookingId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование " +
                bookingId + " не найдено!"));
        if (booking.getBooker().equals(user) || booking.getItem().getOwner().equals(user)) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new ValidationException("Вы не бронировали вещь или не являетесь ее владельцем!");
    }

    @Override
    public Collection<BookingDto> getAllBookingsUser(Long userId, State state) {
        log.info("Получение всех бронирований пользователя {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, Sort.by("start"));
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, timeNow, timeNow, Sort.by("start"));
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrBookerIdAndStatus(userId, Status.REJECTED, userId,
                            Status.CANCELED, Sort.by("start"));
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, timeNow, Sort.by("start"));
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, timeNow, Sort.by("start"));
            case ALL -> bookingRepository.findByBookerId(userId, Sort.by("start"));
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllBookingsOwner(Long userId, State state) {
        log.info("Получение всех бронирований владельца {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case WAITING -> bookingRepository.findByItem_OwnerIdAndStatus(userId, Status.WAITING, Sort.by("start"));
            case CURRENT ->
                    bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfter(userId, timeNow, timeNow, Sort.by("start"));
            case REJECTED ->
                    bookingRepository.findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatus(userId, Status.REJECTED, userId, Status.CANCELED, Sort.by("start"));
            case PAST -> bookingRepository.findByItem_OwnerIdAndEndBefore(userId, timeNow, Sort.by("start"));
            case FUTURE -> bookingRepository.findByItem_OwnerIdAndStartAfter(userId, timeNow, Sort.by("start"));
            case ALL -> bookingRepository.findByItem_OwnerId(userId, Sort.by("start"));
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}