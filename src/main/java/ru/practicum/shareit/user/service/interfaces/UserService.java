package ru.practicum.shareit.user.service.interfaces;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    UserDto createUser(User user);

    UserDto getUser(Long id);

    UserDto updateUser(Long id, User user);

    void deleteUser(Long id);
}
