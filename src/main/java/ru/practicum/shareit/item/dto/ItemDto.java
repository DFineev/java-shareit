package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
