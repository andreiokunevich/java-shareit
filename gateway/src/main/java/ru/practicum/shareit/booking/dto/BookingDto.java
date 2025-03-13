package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull(message = "Айди вещи должен быть указан!")
    private Long itemId;
    @NotNull(message = "Дата начала бронирования должна быть указана!")
    @FutureOrPresent(message = "Дата и время начала бронирования не может быть в прошлом!")
    private LocalDateTime start;
    @NotNull(message = "Дата завершения бронирования должна быть указана!")
    @Future(message = "Дата и время завершения бронирования не может быть в прошлом!")
    private LocalDateTime end;
}