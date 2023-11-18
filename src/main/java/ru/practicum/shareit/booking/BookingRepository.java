package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findAllByBookerIdAndEndIsBefore(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(int userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(int userId, BookingStatus bookingStatus);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(int userId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(int userId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(int userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(int userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemIdOrderByStartAsc(int itemId);

    List<Booking> findByBooker_IdAndItem_IdOrderByStartAsc(int userId, int itemId);
}

