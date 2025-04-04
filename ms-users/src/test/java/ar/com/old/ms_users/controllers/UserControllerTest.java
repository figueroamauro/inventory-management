package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.services.UserService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    UserResponseMapper mapper;

    @Test
    void shouldReturnPageWith3UsersAndStatus200() throws Exception {
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        List<User> list = List.of(new User(1L, "user1", "123123", "user1@mail.com"),
                new User(2L, "user2", "123123", "user2@mail.com"),
                new User(3L, "user3", "123123", "user3@mail.com"));
        when(userService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(list, pageable, list.size()));
        when(mapper.toDto(any(User.class))).thenReturn(new UserResponseDTO(1L, "test", "test"));

        //WHEN
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._embedded.userResponseDTOList.length()").value(list.size()))
                .andDo(print());

    }
}