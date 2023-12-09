package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Integer userId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBefore(Integer userId, LocalDateTime date, Sort sort,PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfter(Integer userId, LocalDateTime date, Sort sort,PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime dateTimeStart, LocalDateTime dateRimeEnd, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(Integer userId, BookingStatus bookingStatus,PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Integer userId, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Integer userId, LocalDateTime date, Sort sort,PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Integer userId, LocalDateTime date, Sort sort,PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime dateTime, LocalDateTime date, Sort sort, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_IdAndStatus(Integer userId, BookingStatus bookingStatus,PageRequest pageRequest);

    List<Booking> findAllByItemIdOrderByStartAsc(Integer itemId);

    List<Booking> findByBooker_IdAndItem_IdOrderByStartAsc(Integer userId, Integer itemId);

}

