package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.user.dto.UserInfoDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInfoDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemFullDto item;
    private UserInfoDto booker;
    private BookingStatus status;
}
