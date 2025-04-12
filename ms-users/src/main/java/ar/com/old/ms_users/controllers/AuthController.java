package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.security.CustomUserDetails;
import ar.com.old.ms_users.security.JwtService;
import ar.com.old.ms_users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserResponseMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserResponseMapper mapper, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        User user = userService.create(dto);

        return ResponseEntity.created(
                        URI.create("/api/users/" + user.getId()))
                .body(mapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserRequestDTO dto) {
        Authentication loginAuthentication = new UsernamePasswordAuthenticationToken(dto.userName(), dto.password());
        Authentication authentication = authenticate(loginAuthentication);

        CustomUserDetails userDetails = getUserDetails(authentication);

        String token = jwtService.generateToken(userDetails);

        Map<String, String> response = buildResponse(token);

        return ResponseEntity.ok(response);
    }


    private Authentication authenticate(Authentication loginAuthentication) {
        return authenticationManager.authenticate(loginAuthentication);
    }

    private static CustomUserDetails getUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private static Map<String, String> buildResponse(String token) {
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }

}
