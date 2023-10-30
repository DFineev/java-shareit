package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    private int id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
