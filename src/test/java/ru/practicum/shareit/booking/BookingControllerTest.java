package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.user.dto.UserInfoDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    private BookingInfoDto bookingInfoDto;

    private BookingDto bookingDto;
    private static final String USERID_HEADER = "X-Sharer-User-Id";


    @BeforeEach
    void setUp() {
        bookingInfoDto = BookingInfoDto.builder()
                .id(1)
                .item(ItemFullDto.builder().id(20).name("Item").build())
                .booker(UserInfoDto.builder()
                        .id(10)
                        .build())
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .bookerId(10)
                .itemId(20)
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBookingIsOkTest() throws Exception {
        when(bookingService.addBooking(anyInt(), any(BookingDto.class)))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USERID_HEADER, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }

    @Test
    void addBookingIsFailedUserIdNotFoundTest() throws Exception {

        when(bookingService.addBooking(100, bookingDto))
                .thenThrow(UserNotFoundException.class);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USERID_HEADER, 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBookingIsFailedBadBookingParameterTest() throws Exception {
        when(bookingService.addBooking(10, bookingDto))
                .thenThrow(ValidateException.class);
        bookingDto.setStart(null);
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USERID_HEADER, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatusIsOkTest() throws Exception {
        when(bookingService.updateBookingStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(patch("/bookings" + "/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }


    @Test
    void getCurrentBookingIsOkTest() throws Exception {
        when(bookingService.getCurrentBooking(anyInt(), anyInt()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(get("/bookings" + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingInfoDto)));
    }

    @Test
    void getCurrentBookingFailedBookingNotFoundTest() throws Exception {
        when(bookingService.getCurrentBooking(anyInt(), anyInt()))
                .thenThrow(ObjectNotFoundException.class);
        mockMvc.perform(get("/bookings" + "/10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingIsOkTest() throws Exception {
        when(bookingService.getBooking(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));

        mockMvc.perform(get("/bookings" + "?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingInfoDto))));

        mockMvc.perform(get("/bookings" + "?state=ALL&size=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingIsFailedBadParameterTest() throws Exception {
        when(bookingService.getBooking(any(), any(), anyInt(), anyInt()))
                .thenThrow(ValidateException.class);

        mockMvc.perform(get("/bookings" + "?state=ALL&size=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnerBookingIsOkTest() throws Exception {
        when(bookingService.getOwnerBooking(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingInfoDto));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingInfoDto))));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL&size=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookingIsFailedTest() throws Exception {
        when(bookingService.getOwnerBooking(any(), any(), anyInt(), anyInt()))
                .thenThrow(ValidateException.class);

        mockMvc.perform(get("/bookings" + "/owner?state=ALL&from=-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}