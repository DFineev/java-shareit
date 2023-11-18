package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {

    List<ItemFullDto> getItems(Integer userId);

    ItemDto addNewItem(int userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto item);

    void deleteItem(int userId, int itemId);

    ItemFullDto getItemByUserIdAndItemId(int userId, int itemId);

    List<ItemDto> searchItems(String query);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);

}
