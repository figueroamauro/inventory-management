package ar.com.old.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(

        @NotBlank(message = "Username can not be blank")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long")
        @Pattern(regexp = USERNAME_PATTERN,
                message = "Invalid username pattern. Must contain only alphanumeric characters and underscores")
        String userName,

        @NotBlank(message = "Password can not be blank")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters long")
        @Pattern(regexp = PASSWORD_PATTERN,
                message = "Invalid password pattern. Must contain 1 number, 1 letter and without whitespaces")
        String password


) {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[^\\s;]+$";
}
