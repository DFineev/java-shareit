package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator {
    public static boolean itemCheck(ItemDto item) {
        return item.getName() == null ||
                item.getName().isBlank() ||
                item.getName().isEmpty() ||
                item.getAvailable() == null ||
                item.getDescription() == null ||
                item.getDescription().isEmpty() ||
                item.getDescription().isBlank();
    }

    public static Item itemPatch(Item firstItem,
                                 ItemDto secondItem) {

        if (secondItem.getName() != null) {
            firstItem.setName(secondItem.getName());
        }
        if (secondItem.getDescription() != null) {
            firstItem.setDescription(secondItem.getDescription());
        }
        if (secondItem.getAvailable() != null) {
            firstItem.setAvailable(secondItem.getAvailable());
        }
        return firstItem;
    }
}