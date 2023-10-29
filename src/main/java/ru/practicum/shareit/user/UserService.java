package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(int id);
    User saveUser(User user);

    User updateUser(int id, User user);
}
