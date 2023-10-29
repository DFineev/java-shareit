package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") int userId,
                    @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId,itemDto);
    }

    @PatchMapping ("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") int userId,
                        @PathVariable("itemId") int itemId,
                        @RequestBody ItemDto itemDto){
        return itemService.updateItem(userId,itemId,itemDto);
    }


    /*
    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") int userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }*/
}
