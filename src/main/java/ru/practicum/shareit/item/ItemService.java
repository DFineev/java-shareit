package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getItems(int userId);

    Item addNewItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, Item item);

    void deleteItem(int userId,int itemId);

    ItemDto getItemByUserIdAndItemId(int userId,int itemId);

}
