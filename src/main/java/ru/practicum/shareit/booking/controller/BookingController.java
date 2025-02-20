package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.service.interfaces.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public Collection<BookingDto> getAllBookingsUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") State state) {
        return bookingService.getAllBookingsUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") State state) {
        return bookingService.getAllBookingsOwner(userId, state);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @RequestBody NewBookingDto newBookingDto) {
        return bookingService.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }
}