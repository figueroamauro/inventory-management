package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    UserResponseMapper mapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnPageWith3Users_status200() throws Exception {
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        List<User> list = List.of(new User(1L, "user1", "123123", "user1@mail.com"),
                new User(2L, "user2", "123123", "user2@mail.com"),
                new User(3L, "user3", "123123", "user3@mail.com"));
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
        User user = new User(1L, "test", "123123", "test@mail.com");
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
    void shouldThrowExceptionFindingById_whenUserNotFound_status404() throws Exception {
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
        UserRequestDTO dto = new UserRequestDTO(null, "test", "123123", "test@mail.com");
        when(userService.create(dto)).thenReturn(new User(1L, "test", "123123", "test@mail.com"));
        verifyMapToDTOMock();

        //WHEN
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.userName").value("test"));

        verify(userService).create(any(UserRequestDTO.class));
    }


    private void verifyMapToDTOMock() {
        when(mapper.toDto(any(User.class))).thenReturn(new UserResponseDTO(1L, "test", "test@mail.com"));
    }

}