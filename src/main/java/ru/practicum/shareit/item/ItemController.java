package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
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
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId,itemDto);
    }

    @PatchMapping ("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId,
                        @PathVariable("itemId") int itemId,
                        @RequestBody Item item){
        return itemService.updateItem(userId,itemId,item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                               @PathVariable int itemId) {
        return itemService.getItemByUserIdAndItemId(userId,itemId);
    }
}
