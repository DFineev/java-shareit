package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Item {
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
