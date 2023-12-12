package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Component
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;

    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Выполнен запрос всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        log.info("Выполнен запрос пользователя по id");
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto user) {
        log.info("Пользователь успешно создан");
        return userService.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId,
                              @RequestBody UserDto user) {
        log.info("Пользователь успешно обновлен");
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Пользователь успешно удален");
        userService.deleteUser(userId);
    }


}
