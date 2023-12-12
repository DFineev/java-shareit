package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {

    List<ItemFullDto> getItems(Integer userId, Integer from, Integer size);

    ItemDto addNewItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto item);

    void deleteItem(Integer userId, Integer itemId);

    ItemFullDto getItemByUserIdAndItemId(Integer userId, Integer itemId);

    List<ItemDto> searchItems(String query, Integer from,Integer size);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);

}
