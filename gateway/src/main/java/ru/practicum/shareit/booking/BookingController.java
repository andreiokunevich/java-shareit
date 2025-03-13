package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingsUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingClient.getAllBookingsUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingClient.getAllBookingsOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam Boolean approved) {
        return bookingClient.confirmationBooking(userId, bookingId, approved);
    }
}
