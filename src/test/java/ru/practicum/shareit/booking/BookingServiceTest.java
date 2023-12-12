package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownBookingState;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private Booking booking;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(User.builder()
                        .id(2)
                        .build())
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
    }

    @Test
    void addBookingTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookingDto.getBookerId())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingService.addBooking(1, bookingDto))
                .thenReturn(BookingMapper.toBookingInfoDto(booking));

        assertThrows(ValidateException.class, () -> {
            BookingDto exceptionBooking = BookingMapper.toBookingDto(booking);
            exceptionBooking.setStart(LocalDateTime.of(2050, Month.FEBRUARY, 25, 10, 0));
            bookingService.addBooking(1, exceptionBooking);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            item.setOwner(user);
            bookingService.addBooking(1, bookingDto);
        });

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(
                Item.builder()
                        .id(1)
                        .name("item")
                        .description("itemDescription")
                        .available(false)
                        .owner(User.builder()
                                .id(2)
                                .build())
                        .build()));

        assertThrows(ValidateException.class, () -> bookingService.addBooking(1, bookingDto));
    }

    @Test
    void updateBookingStatusTest() {
        item.setOwner(user);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingService.updateBookingStatus(1, 1, true))
                .thenReturn(BookingMapper.toBookingInfoDto(booking));

        assertThrows(ValidateException.class, () -> {
            booking.setStatus(BookingStatus.APPROVED);
            bookingService.updateBookingStatus(1, 1, true);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            item.setOwner(User.builder()
                    .id(2)
                    .build());
            bookingService.updateBookingStatus(1, 1, true);
        });

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBookingStatus(1, 1, true));

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBookingStatus(1, 1, true));
    }

    @Test
    void getCurrentBookingTest() {
        Booking bookingTest = Booking.builder()
                .id(1)
                .start(LocalDateTime.of(2023, Month.MAY, 25, 12, 0))
                .end(LocalDateTime.of(2023, Month.MAY, 26, 12, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingTest));

        assertEquals(bookingService.getCurrentBooking(1, 1), BookingMapper.toBookingInfoDto(bookingTest));

        bookingTest.setBooker(User.builder()
                .id(10)
                .build());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getCurrentBooking(1, 1));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getCurrentBooking(1, 999));
    }

    @Test
    void getBookingTest() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(UnknownBookingState.class, () -> bookingService.getBooking(user.getId(), "TEST", 0, 10));

        assertThrows(ValidateException.class, () -> bookingService.getBooking(user.getId(), "TEST", 0, -10));

        assertThrows(ValidateException.class, () -> bookingService.getBooking(user.getId(), "TEST", -1, 10));

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(user.getId(), "ALL", 0, 10));
    }

    @Test
    void getOwnerBookingTest() {
        assertThrows(UnknownBookingState.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", 0, 10));

        assertThrows(ValidateException.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", 0, -10));

        assertThrows(ValidateException.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", -1, 10));

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getOwnerBooking(user.getId(), "ALL", 0, 10));
    }
}