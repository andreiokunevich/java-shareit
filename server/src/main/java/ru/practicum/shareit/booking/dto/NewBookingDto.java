package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingDto {
    @NotNull(message = "Дата начала бронирования не может быть пустой!")
    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем!")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть пустой!")
    @Future(message = "Дата окончания бронирования должна быть в будущем!")
    private LocalDateTime end;

    @NotNull(message = "Вещь для бронирования должна быть указана!")
    private Long itemId;
}