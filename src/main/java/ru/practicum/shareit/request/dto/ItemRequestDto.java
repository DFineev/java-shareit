package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    private Integer id;
    @NotBlank
    @NotNull
    private String description;
    private Integer requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}