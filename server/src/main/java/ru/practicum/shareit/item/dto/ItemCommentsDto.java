package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemCommentsDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым!")
    private String name;

    @NotBlank(message = "Описание не может быть пустым!")
    private String description;

    @NotNull
    private Boolean available;
    private UserDto owner;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
