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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios: actualización, eliminación y consulta de información")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"userName\"}";

    private final UserService userService;
    private final UserResponseMapper mapper;

    public UserController(UserService userService, UserResponseMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de usuarios")
    @GetMapping
    public ResponseEntity<PagedModel<?>> findAll(@Schema(example = DEFAULT_PAGE)@PageableDefault(size = 10 ,sort = "userName", page = 0)
                                                     Pageable pageable,
                                                 PagedResourcesAssembler<UserResponseDTO> assembler) {
        Page<UserResponseDTO> page = userService.findAll(pageable).map(mapper::toDto);

        return ResponseEntity.ok(assembler.toModel(page));
    }


    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener un usuario específico por su id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findOne(@PathVariable Long id) {
        User user = userService.findOne(id);

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Operation(summary = "Actualizar valores de un usuario específico")
    @ApiResponse(responseCode = "200")
    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateRequestDTO dto) {
        User user = userService.update(dto);

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Operation(summary = "Borrar un usuario específico")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Hidden
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
