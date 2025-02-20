package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
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
    public BookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Подтверждение бронирования от пользователя {}, айди бронирования {}", userId, bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование " +
                bookingId + " не найдено!"));
        if (booking.getItem().getOwner().getId().equals(userId) && booking.getStatus().equals(Status.WAITING)) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        throw new ValidationException("Вы не владелец вещи или статус бронирования не WAITING!");
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
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStart(userId, Status.WAITING);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStart(userId, timeNow, timeNow);
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrBookerIdAndStatusOrderByStart(userId, Status.REJECTED, userId, Status.CANCELED);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStart(userId, timeNow);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStart(userId, timeNow);
            case ALL -> bookingRepository.findByBookerIdOrderByStart(userId);
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
            case WAITING -> bookingRepository.findByItem_OwnerIdAndStatusOrderByStart(userId, Status.WAITING);
            case CURRENT ->
                    bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStart(userId, timeNow, timeNow);
            case REJECTED ->
                    bookingRepository.findByItem_OwnerIdAndStatusOrItem_OwnerIdAndStatusOrderByStart(userId, Status.REJECTED, userId, Status.CANCELED);
            case PAST -> bookingRepository.findByItem_OwnerIdAndEndBeforeOrderByStart(userId, timeNow);
            case FUTURE -> bookingRepository.findByItem_OwnerIdAndStartAfterOrderByStart(userId, timeNow);
            case ALL -> bookingRepository.findByItem_OwnerIdOrderByStart(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}