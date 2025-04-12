package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.security.CustomUserDetails;
import ar.com.old.ms_users.security.JwtService;
import ar.com.old.ms_users.security.SecurityConfig;
import ar.com.old.ms_users.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserResponseMapper mapper;
    @MockitoBean
    AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void init() {
        user = new User(1L, "test", "123123", "test@mail.com");
        requestDTO = new UserRequestDTO(1L, "test", "password123", "test@mail.com");
    }

    @Test
    void shouldThrowException_whenHasInvalidFields_status400() throws Exception {
        //GIVEN
        requestDTO = new UserRequestDTO(1L, "bad name", "pas1231232,", "email@@mail.com");

        //WHEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                //THEN
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void shouldCreateUser_status201() throws Exception {
        //GIVEN
        when(userService.create(requestDTO)).thenReturn(user);
        when(mapper.toDto(any(User.class))).thenReturn(new UserResponseDTO(1L, "test", "test@mail.com"));

        //WHEN
        mockMvc.perform(post("/api/auth/register")
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
    void shouldLogin_whenHasCorrectCredentials_status200() throws Exception {
        //GIVEN
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(jwtService.generateToken(customUserDetails)).thenReturn("test-token");


        //WHEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void shouldThrowExceptionLoggingIn_whenHasIncorrectCredentials_status400() throws Exception {
        //GIVEN
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        //WHEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                //THEN
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Bad credentials"));
    }
}