package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.security.CustomUserDetailsService;
import ar.com.old.ms_users.security.JwtService;
import ar.com.old.ms_users.security.SecurityConfig;
import ar.com.old.ms_users.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserResponseMapper mapper;
    @MockitoBean
    CustomUserDetailsService userDetailsService;
    @MockitoBean
    private JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private UserRequestDTO requestDTO;
    private UserUpdateRequestDTO requestUpdateDTO;

    @BeforeEach
    void init() {
        user = new User(1L, "test", "123123", "test@mail.com");
        requestDTO = new UserRequestDTO(1L, "test", "password123", "test@mail.com");
        requestUpdateDTO = new UserUpdateRequestDTO(1L, "test", "test@mail.com");
    }


    @Nested
    class FindAllTest {

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
    }

    @Nested
    class FindByIdTest {

        @Test
        @WithMockUser(roles = "ADMIN")
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
        @WithMockUser(roles = "ADMIN")
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
    }

    @Nested
    class UpdateTest {

        @Test
        @WithMockUser(roles = "USER")
        void shouldUpdateUser_status200() throws Exception {
            //GIVEN

            when(userService.update(requestUpdateDTO)).thenReturn(new User(1L, "test", "123123", "test@mail.com"));
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

            verify(userService).update(any(UserUpdateRequestDTO.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void shouldReturnErrorUpdatingUser_whenIdIsNull_status400() throws Exception {
            //GIVEN
            when(userService.update(requestUpdateDTO))
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

            verify(userService).update(any(UserUpdateRequestDTO.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void shouldReturnErrorUpdatingUser_whenUserNotFound_status400() throws Exception {
            //GIVEN
            when(userService.update(requestUpdateDTO))
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

            verify(userService).update(any(UserUpdateRequestDTO.class));
        }
    }

    @Nested
    class DeleteTest {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteUser_status204() throws Exception {
            //WHEN
            mockMvc.perform(delete("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //THEN
                    .andExpect(status().isNoContent());

            verify(userService).delete(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnErrorDeletingUser_whenNotFound_status404() throws Exception {
            //GIVEN
            doThrow(new UserNotFoundException("User not found")).when(userService).delete(1L);

            //WHEN
            mockMvc.perform(delete("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //THEN
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.error").value("User not found"));

            verify(userService).delete(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnErrorDeletingUser_whenIdIsNull_status400() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException("Id can not be null")).when(userService).delete(1L);

            //WHEN
            mockMvc.perform(delete("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))

                    //THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.error").value("Id can not be null"));

            verify(userService).delete(1L);
        }

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