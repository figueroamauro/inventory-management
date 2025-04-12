package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserResponseMapper mapper;

    public AuthController(UserService userService, UserResponseMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        User user = userService.create(dto);

        return ResponseEntity.created(
                        URI.create("/api/users/" + user.getId()))
                .body(mapper.toDto(user));
    }

}
