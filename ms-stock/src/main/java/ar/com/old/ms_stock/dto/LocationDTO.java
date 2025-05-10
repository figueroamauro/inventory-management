package ar.com.old.ms_stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocationDTO(
        Long id,

        @NotBlank(message = "Location can not be blank")
        @Size(min = 2, max = 20, message = "Location must be between 2 and 20 characters long")
        @Pattern(regexp = NAME_PATTERN, message = "Invalid location name pattern. Must contain only alphanumeric characters")
        String name
) {
    private final static String NAME_PATTERN = "^[A-Za-z0-9]+( [A-Za-z0-9]+)*$";
}
