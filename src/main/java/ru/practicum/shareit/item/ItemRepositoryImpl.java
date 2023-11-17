package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.stream.Collectors;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;
    private final Map<Integer, Item> items = new HashMap<>();
    public int currentId = 1;

    public ItemRepositoryImpl(UserRepository userRepository, ItemMapper itemMapper) {
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<Item> findByUserId(int id) {
        List<Item> itemsList = new ArrayList<>();
        for (Item value : items.values()) {
            if (id == value.getOwnerId()) {
                itemsList.add(value);
            }
        }
        log.info("Запрос выполнен");
        return itemsList;
    }

    @Override
    public Item save(int userId, ItemDto itemDto) {
        if (getUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            Item newItem = itemMapper.toEntity(itemDto, userId);
            newItem.setId(generateId());
            items.put(newItem.getId(), newItem);
            log.info("Предмет добавлен");
            return newItem;
        }
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
        if (userId == 0) {
            throw new UserNotFoundException("Пользователь не указан");
        }
        Item requestedItem = findByItemId(itemId);
        if (userId == requestedItem.getOwnerId()) {
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
            log.info("Запрос на обновление предмета выполнен");
            return itemMapper.toItemDto(requestedItem);
        } else {
            throw new UserNotFoundException("Пользователь не авторизован");
        }
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (findByItemId(itemId).getOwnerId() == userId) {
            items.remove(itemId);
            log.info("Запрос на удаление предмета выполнен");
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Item findByItemId(int itemId) {
        log.info("Запрос выполнен");
        return items.get(itemId);
    }

    @Override
    public ItemDto getItemByUserIdAndItemId(int userId, int itemId) {
        log.info("Запрос выполнен");
        return itemMapper.toItemDto(findByItemId(itemId));
    }

    @Override
    public List<ItemDto> searchItems(int userId, String query) {
        List<ItemDto> selection = new ArrayList<>();
        if (!query.isEmpty()) {
            selection = items.values().stream()
                    .filter(v -> v.getName().toLowerCase().contains(query.toLowerCase()) || v.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .filter(v -> v.getAvailable())
                    .map(v -> itemMapper.toItemDto(v))
                    .collect(Collectors.toList());
            log.info("Поисковый запрос выполнен");
        } else {
            log.info("Поисковый запрос пустой");
        }
        return selection;
    }

    private User getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    private int generateId() {
        return currentId++;
    }

}

