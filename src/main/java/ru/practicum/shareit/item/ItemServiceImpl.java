package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getItems(int userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Item addNewItem(int userId, ItemDto itemDto) {
        return itemRepository.save(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        itemRepository.deleteItem(userId, itemId);
    }

    @Override
    public ItemDto getItemByUserIdAndItemId(int userId, int itemId) {
        return itemRepository.getItemByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> searchItems(int userId, String query) {
        return itemRepository.searchItems(userId, query);
    }
}
