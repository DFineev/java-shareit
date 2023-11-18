package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer itemId;

    private Integer bookerId;

    private BookingStatus status;

}
