package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findByUserId(int id);

    Item save(int id, ItemDto itemDto);

    ItemDto updateItem(int id, int itemId, Item item);

  //  Item findByItemId(int id);

    void deleteItem(int userId,int itemId);
    ItemDto getItemByUserIdAndItemId(int userId,int itemId);
}
