package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;


    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    void getUsers() {
        when(repository.findAll()).thenReturn(List.of(user));
        System.out.println(repository.findAll());
        assertEquals(service.getAllUsers(), List.of(UserMapper.toUserDto(user)));
    }

    @Test
    void getUser() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(user));

        assertEquals(service.getUserById(1), UserMapper.toUserDto(user));

        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserById(10));
    }

    @Test
    void addUser() {
        when(repository.save(any())).thenReturn(user);

        assertEquals(service.saveUser(UserMapper.toUserDto(user)), UserMapper.toUserDto(user));

        when(repository.existsUserByEmail(any())).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.saveUser(UserMapper.toUserDto(user)));

        user.setEmail(null);
        assertThrows(ValidateException.class, () -> service.saveUser(UserMapper.toUserDto(user)));
    }

    @Test
    void updateUser() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("update")
                .email("update@email.com")
                .build();

        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(user);

        assertEquals(service.updateUser(1, userDto), UserMapper.toUserDto(user));

        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateUser(1, userDto));


    }

    @Test
    void deleteUser() {
        when(repository.existsById(anyInt())).thenReturn(true);
        service.deleteUser(1);
        verify(repository).deleteById(1);

        when(repository.existsById(anyInt())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> service.deleteUser(999));
    }
}