package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

   List<User> findAll();

   User getUserById(int id);

    User save(User user);
    User updateUser(int id,User user);

    void deleteUser(int userId);

}
