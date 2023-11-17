package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody BookingDto booking) {
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer bookingId,
                                              @RequestParam Boolean approved) {

        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getCurrentBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable Integer bookingId) {
        return bookingService.getCurrentBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        return bookingService.getBooking(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        return bookingService.getOwnerBooking(userId, stateParam);
    }
}