package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Email должен быть указан!")
    @Email
    private String email;
}