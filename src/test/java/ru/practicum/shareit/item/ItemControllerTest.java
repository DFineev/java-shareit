package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private ItemFullDto itemFullDto;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        itemFullDto = ItemFullDto.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        commentDto = CommentDto.builder()
                .id(1)
                .text("commentText")
                .authorName("commentAuthorName")
                .build();
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1, 0, 10))
                .thenReturn(List.of(itemFullDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("itemName")))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].comments", is(Collections.emptyList())));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItemByUserIdAndItemId(anyInt(), anyInt()))
                .thenReturn(itemFullDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.comments", is(Collections.emptyList())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addNewItem(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("commentText")))
                .andExpect(jsonPath("$.authorName", is("commentAuthorName")));
    }

    @Test
    void updateItem() throws Exception {

        ItemDto updatedItem = itemDto;
        updatedItem.setName("update");
        updatedItem.setDescription("update");

        when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class)))
                .thenReturn(updatedItem);


        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.description", is("update")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .deleteItem(anyInt(), anyInt());
    }

    @Test
    void searchItems() throws Exception {

        ItemDto secondItem = ItemDto.builder()
                .id(2)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .build();

        when(itemService.searchItems("description", 0, 10))
                .thenReturn(List.of(itemDto, secondItem));


        mockMvc.perform(get("/items/search")
                        .param("text", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("itemName")))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2Name")))
                .andExpect(jsonPath("$[1].description", is("item2Description")))
                .andExpect(jsonPath("$[1].available", is(true)));

    }
}