package ru.practicum.shareit.booking.service.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.enums.State;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, NewBookingDto newBookingDto);

    BookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    Collection<BookingDto> getAllBookingsUser(Long userId, State state);

    Collection<BookingDto> getAllBookingsOwner(Long userId, State state);
}