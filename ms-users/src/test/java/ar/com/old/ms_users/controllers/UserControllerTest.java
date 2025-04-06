package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserResponseMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void init() {
        user = new User(1L, "test", "123123", "test@mail.com");
        requestDTO = new UserRequestDTO(1L, "test", "123123", "test@mail.com");
    }

    @Test
    void shouldReturnPageWith3Users_status200() throws Exception {
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        List<User> list = getUserList();
        when(userService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(list, pageable, list.size()));
        verifyMapToDTOMock();

        //WHEN
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.userResponseDTOList.length()").value(list.size()));

        verify(userService).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindUserById_status200() throws Exception {
        //GIVEN
        when(userService.findOne(1L)).thenReturn(user);
        verifyMapToDTOMock();

        //WHEN
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("test"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));

        verify(userService).findOne(1L);
    }

    @Test
    void shouldReturnErrorFindingById_whenUserNotFound_status404() throws Exception {
        //GIVEN
        when(userService.findOne(1L))
                .thenThrow(new UserNotFoundException("User not found"));

        //WHEN
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(userService).findOne(1L);
    }

    @Test
    void shouldCreateUser_status201() throws Exception {
        //GIVEN
        when(userService.create(requestDTO)).thenReturn(new User(1L, "test", "123123", "test@mail.com"));
        verifyMapToDTOMock();

        //WHEN
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                requestDTO)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.userName").value("test"));

        verify(userService).create(any(UserRequestDTO.class));
    }

    @Test
    void shouldUpdateUser_status200() throws Exception {
        //GIVEN

        when(userService.update(requestDTO)).thenReturn(new User(1L, "test", "123123", "test@mail.com"));
        verifyMapToDTOMock();

        //WHEN
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                requestDTO)))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.userName").value("test"));

        verify(userService).update(any(UserRequestDTO.class));
    }

    @Test
    void shouldReturnErrorUpdatingUser_whenIdIsNull_status400() throws Exception {
        //GIVEN
        when(userService.update(requestDTO))
                .thenThrow(new IllegalArgumentException("Id can not be null"));

        //WHEN
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                requestDTO)))

                //THEN
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Id can not be null"));

        verify(userService).update(any(UserRequestDTO.class));
    }

    @Test
    void shouldReturnErrorUpdatingUser_whenUserNotFound_status400() throws Exception {
        //GIVEN
        when(userService.update(requestDTO))
                .thenThrow(new UserNotFoundException("User not found"));

        //WHEN
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                requestDTO)))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(userService).update(any(UserRequestDTO.class));
    }

    @Test
    void shouldDeleteUser_status204() throws Exception {
        //WHEN
        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }




    private void verifyMapToDTOMock() {
        when(mapper.toDto(any(User.class))).thenReturn(new UserResponseDTO(1L, "test", "test@mail.com"));
    }

    private static @NotNull List<User> getUserList() {
        return List.of(new User(1L, "user1", "123123", "user1@mail.com"),
                new User(2L, "user2", "123123", "user2@mail.com"),
                new User(3L, "user3", "123123", "user3@mail.com"));
    }

}