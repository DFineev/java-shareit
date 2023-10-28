package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
@Repository
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;
    private final List<Item> items = new ArrayList<>();
    public int nextId = 1;

    public ItemRepositoryImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public List<Item> findByUserId(int id) {
        List<Item> itemsList = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId() == id) {
                itemsList.add(item);
            }
        }
        return itemsList;
    }

    @Override
    public Item save(int id, ItemDto itemDto){
        Item newItem = itemMapper.toEntity(itemDto);
        newItem.setId(nextId++);
        items.add(newItem);
        return newItem;
    }

}
