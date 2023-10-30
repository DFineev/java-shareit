package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Component
public class ItemMapper {

      private final UserRepository userRepository;


    public ItemMapper(UserRepository userRepository) {
                this.userRepository = userRepository;
    }

    public Item toEntity(ItemDto dto, int ownerId) {
        return new Item(
                dto.getId(),
                obtainOwner(ownerId),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getRequest()
        );
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest()// != null ? //item.getRequest().getId() : null
        );
    }

    private User obtainOwner(int ownerId) {
                return userRepository.getUserById(ownerId);
    }

    private int obtainOwnerId(Item item) {
        return item.getOwner().getId();
    }

}
