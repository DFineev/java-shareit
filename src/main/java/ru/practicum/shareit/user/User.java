package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
public class User {
    private int id;
    @Email(message = "Неверный формат электронной почты")
    private String email;
    private String name;
}