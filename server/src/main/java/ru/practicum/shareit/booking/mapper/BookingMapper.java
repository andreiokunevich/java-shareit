package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(User user, Item item, NewBookingDto newBookingDto) {
        Booking booking = new Booking();
        booking.setStart(newBookingDto.getStart());
        booking.setEnd(newBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.userToDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
}