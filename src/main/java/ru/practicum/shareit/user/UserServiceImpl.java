package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        return users;
    }

    @Override
    public UserDto getUserById(int id) {
        return repository.findById(id).map(UserMapper::toUserDto).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Transactional
    @Override
    public User saveUser(UserDto user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            throw new ValidateException("Не указан email пользователя");
        }
        return repository.save(UserMapper.toUser(user));
    }

    @Override
    public UserDto updateUser(int id, UserDto user) {
        User originUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        log.info("Пользователь обновлен");
        return UserMapper.toUserDto(repository.save(updateUserNameAndEmail(originUser, user)));
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteById(userId);
    }

    private User updateUserNameAndEmail(User updatedUser, UserDto user) {
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (!repository.existsUserByEmail(user.getEmail())) {
                updatedUser.setEmail(user.getEmail());
            } else {
                throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            }
        }
        return updatedUser;
    }
}
