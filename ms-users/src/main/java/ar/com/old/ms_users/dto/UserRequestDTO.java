package ar.com.old.ms_users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        Long id,

        @NotBlank(message = "Username can not be blank")
        String userName,

        String password,

        String email) {
}
