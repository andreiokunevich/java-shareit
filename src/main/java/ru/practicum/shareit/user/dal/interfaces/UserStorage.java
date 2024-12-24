package ru.practicum.shareit.user.dal.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    Optional<User> getUser(Long id);

    User updateUser(Long id, User newUser);

    void deleteUser(Long id);

    boolean isEmailTaken(User user);
}