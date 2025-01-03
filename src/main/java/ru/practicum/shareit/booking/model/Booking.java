package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@RequiredArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}
