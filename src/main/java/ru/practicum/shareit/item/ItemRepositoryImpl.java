package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;
    private final Map<Integer,Item> items = new HashMap<>();
    public int nextId = 1;

    public ItemRepositoryImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public List<Item> findByUserId(int id) {
        List<Item> itemsList = new ArrayList<>();
        for (Item value : items.values()) {
            if (id == value.getOwner().getId()) {
                itemsList.add(value);
            }
        }
        return itemsList;
    }

    @Override
    public Item save(int id, ItemDto itemDto) {
        Item newItem = itemMapper.toEntity(itemDto,id);
        newItem.setId(nextId++);
        items.put(newItem.getId(),newItem);
        return newItem;
    }

    @Override
    public Item updateItem(int userId, int itemId, ItemDto itemDto) {
        Item requestedItem = findByItemId(itemId);
        if (userId == requestedItem.getOwner().getId()) {
            Item updatedItem = itemMapper.toEntity(itemDto, userId);
            items.put(itemId,updatedItem);
            return updatedItem;
        } else throw new RuntimeException();
    }

    @Override
    public Item findByItemId(int itemId) {
        return items.get(itemId);
    }
}

