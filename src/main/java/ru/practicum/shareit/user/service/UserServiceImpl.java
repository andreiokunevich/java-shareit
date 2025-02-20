package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.interfaces.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Попытка создания пользователя");
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null) {
            throw new ValidationException("При создании пользователя почта должна быть указана!");
        }
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("Попытка получить пользователя по айди {}", id);
        return UserMapper.userToDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + id + " не найден!")));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Попытка обновить пользователя");
        User user = UserMapper.toUser(userDto);
        User userFromStorage = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + id + " не найден!"));
        String name = user.getName();
        String email = user.getEmail();
        if (!(name == null || name.isBlank())) {
            userFromStorage.setName(name);
        }
        if (!(email == null || email.isBlank())) {
            userFromStorage.setEmail(email);
        }
        return UserMapper.userToDto(userRepository.save(userFromStorage));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с айди {}", id);
        userRepository.deleteById(id);
    }
}