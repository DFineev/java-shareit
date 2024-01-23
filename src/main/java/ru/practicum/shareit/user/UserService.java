package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(int id);

    UserDto saveUser(UserDto user);

    UserDto updateUser(int id, UserDto user);

    void deleteUser(int userId);
}
