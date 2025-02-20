package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым!")
    private String name;

    @NotBlank(message = "Описание не может быть пустым!")
    private String description;

    @NotNull
    private Boolean available;
    private UserDto owner;
}