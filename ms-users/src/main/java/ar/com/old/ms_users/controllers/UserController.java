package ar.com.old.ms_users.controllers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.mappers.UserResponseMapper;
import ar.com.old.ms_users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


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

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        User user = userService.create(dto);
        return ResponseEntity.created(
                        URI.create("/api/users/" + user.getId()))
                .body(mapper.toDto(user));
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
}
