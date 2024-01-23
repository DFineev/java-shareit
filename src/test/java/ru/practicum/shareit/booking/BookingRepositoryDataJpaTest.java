package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryDataJpaTest {

    @Autowired
    @MockBean
    private BookingService bookingService;
    @Autowired
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private Booking booking;

    private Sort sort;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("user")
                .email("user@user.com")
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("user2@user.com")
                .build();

        Item item = Item.builder()
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(user2)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(booking);
        sort = Sort.by(Sort.Direction.ASC, "start");
    }


    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(1, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        bookingList = bookingRepository.findAllByBookerIdAndEndIsBefore(1, end, sort, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(1, end, sort, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(1,
                booking.getStart().plusHours(1), booking.getEnd().minusHours(1), sort, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBookerIdAndStatusTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        bookingList = bookingRepository.findAllByBookerIdAndStatus(1, BookingStatus.REJECTED, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());

        booking.setStatus(BookingStatus.WAITING);

        bookingList = bookingRepository.findAllByBookerIdAndStatus(1, BookingStatus.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDescTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        bookingList = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(2, PageRequest.of(0, 10));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsBeforeTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        bookingList = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(2, end, sort, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByItem_Owner_IdAndStartIsAfterTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        bookingList = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(2, end, sort, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByItem_Owner_IdAndStatusTest() {
        List<Booking> bookingList = new ArrayList<>();
        assertEquals(0, bookingList.size());

        bookingList = bookingRepository.findAllByItem_Owner_IdAndStatus(2, BookingStatus.REJECTED, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());

        booking.setStatus(BookingStatus.WAITING);

        bookingList = bookingRepository.findAllByItem_Owner_IdAndStatus(2, BookingStatus.WAITING, PageRequest.of(0, 1));
        assertEquals(1, bookingList.size());
    }

}