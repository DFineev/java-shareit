package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;
    private final Map<Integer, Item> items = new HashMap<>();
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
        Item newItem = itemMapper.toEntity(itemDto, id);
        newItem.setId(nextId++);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {

        if (userId == 0) throw new UserNotFoundException("Пользователь не указан");
        Item requestedItem = findByItemId(itemId);
        if (userId == requestedItem.getOwner().getId()) {
            item.setId(itemId);
            if (item.getName() != null) {
                requestedItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                requestedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                requestedItem.setAvailable(item.getAvailable());
            }
            items.put(itemId, requestedItem);
            return itemMapper.toItemDto(requestedItem);
        } else throw new UserNotFoundException("Пользователь не авторизован");
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (findByItemId(itemId).getOwner().getId() == userId) {
            items.remove(itemId);
        } else throw new UserNotFoundException("Пользователь не найден");
    }

    public Item findByItemId(int itemId) {
        return items.get(itemId);
    }

    @Override
    public ItemDto getItemByUserIdAndItemId(int userId, int itemId) {
        return itemMapper.toItemDto(findByItemId(itemId));
    }

    @Override
    public List<ItemDto> searchItems(int userId, String query) {
        List<ItemDto> selection = new ArrayList<>();
        if (query.isEmpty()) {
            selection.clear();
        } else {
            for (Item value : items.values()) {
                if ((value.getName().toLowerCase().contains(query.toLowerCase()) || value.getDescription().toLowerCase().contains(query.toLowerCase())) && (value.getAvailable())) {
                    selection.add(itemMapper.toItemDto(value));
                }
            }
        }
        return selection;
    }

}

