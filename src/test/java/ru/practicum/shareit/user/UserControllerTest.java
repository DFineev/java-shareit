package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(UserMapper.toUserDto(user)));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(UserMapper.toUserDto(user)))));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(UserMapper.toUserDto(user));

        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(UserMapper.toUserDto(user))));
    }

    @Test
    void saveUser() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(UserMapper.toUserDto(user));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(UserMapper.toUserDto(user)))
                        .header("X-Sharer-User-Id", 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(UserMapper.toUserDto(user))));
    }

    @Test
    void updateUser() throws Exception {
        User updatedUser = user;
        updatedUser.setName("update");

        when(userService.updateUser(anyInt(), any()))
                .thenReturn(UserMapper.toUserDto(user));

        mockMvc.perform(patch("/users/{userId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(new ObjectMapper().writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.email", is("user@user.com")));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users" + "/1"))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteUser(anyInt());
    }
}