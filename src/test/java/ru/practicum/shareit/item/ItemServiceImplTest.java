package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingItemDto;
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
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.booking.BookingRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    @Autowired
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;
    private Booking booking;
    private Item item;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp(){
                user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(user)
                .start(LocalDateTime.of(2023, Month.FEBRUARY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.FEBRUARY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1)
                .item(item)
                .text("commentText")
                .user(user)
                .created(LocalDateTime.now())
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .requestor(user)
                .description("user2")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1)
                .name("item1")
                .description("item1")
                .owner(user)
                .request(itemRequest)
                .available(true)
                .build();
    }

    @Test
    void getItems() {

     Booking booking1 = booking;
     booking1.setId(2);

     when(commentRepository.existsById(item.getId())).thenReturn(true);
     when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(item)));
     when(commentRepository.findByItem_Id(item.getId())).thenReturn(List.of(comment));
     when(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId())).thenReturn(List.of(booking, booking1));
     when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
     List<ItemFullDto> items = itemService.getItems(user.getId(),0,10);
        assertNotNull(items);
        assertEquals(items.get(0).getId(), item.getId());
    }

    @Test
    void getItem() {
        item.setOwner(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemByUserIdAndItemId (1, 2));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemByUserIdAndItemId(1, 2));
        when(commentRepository.existsById(item.getId())).thenReturn(true);
        when(commentRepository.getReferenceById(item.getId())).thenReturn(comment);
        when(bookingRepository.findAllByItemIdOrderByStartAsc(anyInt()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyInt(),any(LocalDateTime.class),any(Sort.class),any(PageRequest.class))).thenReturn(List.of(booking));

        ItemFullDto result = itemService.getItemByUserIdAndItemId (1, 1);
        assertNotNull(result);
        assertEquals(result.getLastBooking().getId(), booking.getId());
        assertNull(result.getNextBooking());
    }
    @Test
    void addItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(UserNotFoundException.class, () -> itemService.addNewItem(999, itemDto));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.toItem(itemDto));

        itemDto.setRequestId(2);
        assertThrows(ObjectNotFoundException.class, () -> itemService.addNewItem(user.getId(), itemDto));

        itemDto.setRequestId(1);
        ItemDto addedItem = itemService.addNewItem(user.getId(), itemDto);
        assertNotNull(addedItem);

        itemDto.setRequestId(null);
        addedItem = itemService.addNewItem(user.getId(), itemDto);
        assertNull(addedItem.getRequestId());

        assertThrows(ValidateException.class, () -> {
            itemDto.setName("");
            itemService.addNewItem(user.getId(), itemDto);
        });
    }

    @Test
    void deleteItem() {
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(1,999));

        itemService.deleteItem(1,1);
        verify(itemRepository).deleteById(1);
    }

    @Test
    void searchItems() {
        ItemDto itemDto = ItemMapper.toDto(item);
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(itemService.searchItems("",0,10)).thenReturn(Collections.emptyList());
      assertTrue(itemRepository.search("",pageRequest).isEmpty());

      when(itemService.searchItems("item1",0,10)).thenReturn(List.of(itemDto));
     assertFalse(itemRepository.search("item1",pageRequest).isEmpty());
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ValidateException.class, () -> itemService.addComment(2, 1, commentDto));

        ItemFullDto itemInfoDto = mock(ItemFullDto.class);
        BookingItemDto bookingItemDto = BookingItemDto.builder().build();
        bookingItemDto.setId(1);

        when(itemInfoDto.getLastBooking()).thenReturn(bookingItemDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class, () ->
                itemService.addComment(1, 2, commentDto));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);
        when(bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(Item.builder()
                .id(2)
                .owner(User.builder().id(10).build())
                .build()));

        assertNotNull(itemService.addComment(1, 1, commentDto));

        assertThrows(ValidateException.class, () -> {
            commentDto.setText("");
            itemService.addComment(1, 1, commentDto);
        });

        when(bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        assertThrows(ValidateException.class, () -> {
            commentDto.setText("text");
            booking.setEnd(LocalDateTime.of(2050, Month.FEBRUARY, 5, 12, 0));
            itemService.addComment(1, 1, commentDto);
        });

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(ValidateException.class, () -> {
            item.setOwner(User.builder().id(10).build());
            itemService.addComment(1, 1, commentDto);
        });
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        ItemDto updatedItemDto = ItemDto.builder()
                .id(1)
                .name("itemUpdated")
                .description("itemUpdated")
                .owner(user.getId())
                .requestId(itemRequest.getId())
                .available(false)
                .build();

        Item updatedItem = ItemMapper.toItem(updatedItemDto);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        assertEquals(itemRequest.getId(), itemService.updateItem(1, 1, updatedItemDto).getRequestId());

        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1, 1, itemDto));

        when(itemRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemDto.setOwner(1);
            itemService.updateItem(1, 1, itemDto);
        });

        when(itemRepository.findById(1)).thenReturn(Optional.of(Item.builder()
                .id(1)
                .name("item1")
                .description("item1")
                .owner(User.builder().id(1000).build())
                .request(itemRequest)
                .available(true)
                .build()));

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1, 1, itemDto));

        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(1, 1, updatedItemDto));
    }
}