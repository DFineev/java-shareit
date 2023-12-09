package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class BookingRepositoryTest {
    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();

        Item item = Item.builder()
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
    void findAllByBookerIdOrderByStartDescTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "ALL", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyInt(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "PAST", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByBookerIdAndStartIsAfterTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyInt(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "FUTURE", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "CURRENT", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByBookerIdAndStatusTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "WAITING", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "REJECTED", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDescTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyInt(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "ALL", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsBeforeTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(anyInt(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "PAST", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByItem_Owner_IdAndStartIsAfterTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(anyInt(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "FUTURE", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any(), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "CURRENT", 0, 10), List.of(bookingInfoDto));
    }

    @Test
    void findAllByItem_Owner_IdAndStatusTest() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyInt(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "WAITING", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyInt(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "REJECTED", 0, 10), List.of(bookingInfoDto));
    }
}