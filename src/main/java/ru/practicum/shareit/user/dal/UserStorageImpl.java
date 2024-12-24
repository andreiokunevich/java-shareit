package ru.practicum.shareit.user.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dal.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(Long id, User newUser) {
        users.put(id, newUser);
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public boolean isEmailTaken(User user) {
        Optional<User> emailUser = users.values().stream()
                .filter(user1 -> user.getEmail() != null && user.getEmail().equals(user1.getEmail())).findAny();
        return emailUser.isPresent();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}