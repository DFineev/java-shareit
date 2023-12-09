package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemFullDto> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Выполнен запрос объектов пользователя");
        return itemService.getItems(userId,from,size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") int userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Объект успешно добавлен");
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable("itemId") Integer itemId,
                          @RequestBody ItemDto item) {
        log.info("Объект успешно обновлен");
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
        log.info("Объект успешно удален");
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getItemById(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                   @PathVariable int itemId) {
        log.info("Выполнен запрос объекта");
        return itemService.getItemByUserIdAndItemId(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Выполнен поисковый запрос по объектам");
        return itemService.searchItems(text,from,size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Комментарий добавлен");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
