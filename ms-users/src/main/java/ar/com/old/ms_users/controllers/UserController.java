package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.security.CustomUserDetails;
import ar.com.old.ms_users.security.CustomUserDetailsService;
import ar.com.old.ms_users.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserResponseMapper mapper;

    public UserController(UserService userService, UserResponseMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<PagedModel<?>> findAll(Pageable pageable,
                                                 PagedResourcesAssembler<UserResponseDTO> assembler) {
        Page<UserResponseDTO> page = userService.findAll(pageable).map(mapper::toDto);

        return ResponseEntity.ok(assembler.toModel(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findOne(@PathVariable Long id) {
        User user = userService.findOne(id);

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateRequestDTO dto) {
        User user = userService.update(dto);

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser() {
        try {
            CustomUserDetails userDetail = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(mapper.toDto(userDetail.getUser()));
        } catch (ClassCastException e) {
            throw new JwtException("Invalid or null token");
        }
    }
}
