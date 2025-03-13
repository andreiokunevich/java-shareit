package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> captor;

    @Test
    @DisplayName("UserService_getUser_Ok")
    void getUser_whenUserIsValid_thenReturnUserDto() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("name");
        userDto.setEmail("12345@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUser(userId);

        assertEquals(userDto.getId(), actualUserDto.getId());
        assertEquals(userDto.getName(), actualUserDto.getName());
        assertEquals(userDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    @DisplayName("UserService_getUser_NotOk")
    void getUser_whenUserNotFound_thenThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    @DisplayName("UserService_createUser_Ok")
    void createUser_whenUserEmailIsValid_thenReturnUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("12345@mail.ru");

        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUserDto = userService.createUser(userDto);

        assertEquals(userDto.getName(), actualUserDto.getName());
        assertEquals(userDto.getEmail(), actualUserDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("UserService_DeleteUser_Ok")
    void deleteUser_whenUserIsFound_returnNothing() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("12345@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("UserService_DeleteUser_NotOk")
    void deleteUser_whenUserIsNotFound_throwException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("UserService_UpdateUser")
    void updateUser_whenUserHasNameEmail_thenReturnUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("12345@mail.ru");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name111");
        oldUser.setEmail("56789@mail.ru");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("name");
        newUser.setEmail("12345@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.updateUser(1L, userDto);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals(oldUser.getId(), savedUser.getId());
        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("UserService_UpdateUser")
    void updateUser_whenUserNoNameEmail_thenReturnOldUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName(null);
        userDto.setEmail(null);

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name111");
        oldUser.setEmail("56789@mail.ru");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("name111");
        newUser.setEmail("56789@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.updateUser(1L, userDto);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals(oldUser.getId(), savedUser.getId());
        assertEquals(oldUser.getName(), savedUser.getName());
        assertEquals(oldUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("UserService_UpdateUser")
    void updateUser_whenUserBlankNameEmail_thenReturnOldUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("");
        userDto.setEmail("");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name111");
        oldUser.setEmail("56789@mail.ru");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("name111");
        newUser.setEmail("56789@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.updateUser(1L, userDto);

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals(oldUser.getId(), savedUser.getId());
        assertEquals(oldUser.getName(), savedUser.getName());
        assertEquals(oldUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("UserService_UpdateUser")
    void updateUser_whenUserNotFound_thenThrowException() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("12345@mail.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, userDto));
    }
}