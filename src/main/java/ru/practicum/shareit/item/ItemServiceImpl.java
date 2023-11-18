package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public List<ItemFullDto> getItems(Integer userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemFull)
                .peek(this::setBookingToItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(int userId, ItemDto itemDto) {
        if (ItemValidator.itemCheck(itemDto)) {
            throw new ValidateException("Валидация не пройдена");
        }
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found.")));
        return ItemMapper.toDto(itemRepository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto item) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Item not found."));
        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Item not belongs to this user.");
        }
        itemUpdate(updatedItem, item);
        return ItemMapper.toDto(itemRepository.save(updatedItem));
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFoundException("Объект не найден");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemFullDto getItemByUserIdAndItemId(int userId, int itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Объект не найден"));

        ItemFullDto itemFullDto = ItemMapper.toItemFull(item);

        if (item.getOwner().getId() == userId) {
            setBookingToItem(itemFullDto);
        }

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemFullDto.setComments(comments.isEmpty() ? Collections.emptyList() : comments);

        return itemFullDto;
    }

    @Override
    public List<ItemDto> searchItems(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(query).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        validateComment(userId, itemId, commentDto);
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Item not found.")));
        comment.setUser(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("User not found.")));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setBookingToItem(ItemFullDto item) {
        List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());
        if (!bookingList.isEmpty()) {
            Booking lastBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isBefore(LocalDateTime.now()))
                    .reduce((booking, booking2) -> booking2)
                    .orElse(null);
            if (lastBooking != null) {
                item.setLastBooking(BookingMapper.toBookingItem(lastBooking));
            }
            Booking nextBooking = bookingList.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) &&
                            booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);
            if (nextBooking != null) {
                item.setNextBooking(BookingMapper.toBookingItem(nextBooking));
            }
        }
    }

    private Item itemUpdate(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            updatedItem.setOwner(userRepository.findById(itemDto.getOwner())
                    .orElseThrow(() -> new ObjectNotFoundException("User not found.")));
        }
        if (itemDto.getRequest() != null) {
            updatedItem.setRequest(new ItemRequest());
        }
        return updatedItem;
    }

    private void validateComment(Integer userId, Integer itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidateException("Текст комментария отсутствует");
        }
        if (!isAlreadyBooked(userId, itemId)) {
            throw new ValidateException("Пользователь еще не вернул объект");
        }
        if (isOwner(userId, itemId)) {
            throw new ValidateException("Владелец не может комментировать свой объект");
        }
    }

    private Boolean isAlreadyBooked(Integer userId, Integer itemId) {
        List<Booking> bookingList = bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(userId, itemId);
        if (bookingList.isEmpty()) {
            throw new ValidateException("Пользователь не арендовал объект");
        }
        return bookingList.stream()
                .anyMatch(booking ->
                        booking.getEnd().isBefore(LocalDateTime.now()));
    }

    private Boolean isOwner(Integer userId, Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("User not found.")).getOwner().getId().equals(userId);
    }
}
