package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {


    public Item toEntity(ItemDto dto, int userId) {
        return Item.builder()
                .id(dto.getId())
                .ownerId(userId)
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .request(dto.getRequest())
                .build();
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();

    }
}
