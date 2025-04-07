package ar.com.old.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        Long id,

        @NotBlank(message = "Username can not be blank")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long")
        @Pattern(regexp = WITHOUT_WHITESPACES, message = "Username must not contain spaces")
        String userName,

        String password,

        String email
) {
    private static final String WITHOUT_WHITESPACES = "^\\S+$";
}
