package ar.com.old.ms_users.dto;

import jakarta.validation.constraints.*;

public record UserUpdateRequestDTO(
        @NotNull(message = "Id can not be null")
        Long id,

        @NotBlank(message = "Username can not be blank")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long")
        @Pattern(regexp = USERNAME_PATTERN,
                message = "Invalid username pattern. Must contain only alphanumeric characters and underscores")
        String userName,

        @NotBlank(message = "Email can not be blank")
        @Email(message = "Invalid email pattern")
        @Size(max = 255, message = "Email can not be longer than 255 characters")
        String email
) {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
}
