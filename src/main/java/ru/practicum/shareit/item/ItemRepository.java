package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findByUserId(int id);

    Item save(int id, ItemDto itemDto);

    Item updateItem(int id, int itemId, ItemDto itemDto);

    Item findByItemId(int id);
}
