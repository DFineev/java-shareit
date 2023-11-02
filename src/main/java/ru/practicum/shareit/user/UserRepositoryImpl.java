package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    public int currentId = 1;

    @Override
    public List<User> findAll() {
        log.info("Запрос выполнен");
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (user.getEmail() == null) {
            log.info("Валидация не пройдена");
            throw new ValidateException("Не указан email пользователя");
        }
        validator(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        log.info("Запрос выполнен");
        return users.get(id);
    }

    @Override
    public User updateUser(int id, User user) {
        User originUser = users.get(id);
        user.setId(id);
        validator(user);
        if (user.getEmail() == null) {
            user.setEmail(originUser.getEmail());
        }
        if (user.getName() == null) {

            user.setName(originUser.getName());
        }
        users.put(id, user);
        log.info("Запрос на обновление пользователя выполнен");
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Запрос на удаление выполнен");
        users.remove(userId);
    }


    private void validator(User user) {
        for (User value : users.values()) {
            if (Objects.equals(value.getEmail(), user.getEmail()) && user.getId() != value.getId()) {
                log.info("Валидация не пройдена");
                throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            }
        }
    }

    private int generateId() {
        return currentId++;
    }
}
