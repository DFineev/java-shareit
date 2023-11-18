package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody BookingDto booking) {
        log.info("Бронирование добавлено");
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer bookingId,
                                              @RequestParam Boolean approved) {
        log.info("Статус бронирования обновлен");
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable Integer bookingId) {
        log.info("Выполнен запрос бронирования по id");
        return bookingService.getCurrentBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Выполнен запрос бронирований " + stateParam);
        return bookingService.getBooking(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        log.info("Выполнен запрос бронирований владельца " + stateParam);
        return bookingService.getOwnerBooking(userId, stateParam);
    }
}