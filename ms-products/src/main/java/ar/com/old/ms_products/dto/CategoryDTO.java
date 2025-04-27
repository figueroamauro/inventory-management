package ar.com.old.ms_products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryDTO(
        Long id,
        @NotBlank(message = "Category can not be blank")
        @Size(min = 4, max = 20, message = "Category must be between 4 and 20 characters long")
        @Pattern(regexp = NAME_PATTERN,message = "Invalid category name pattern. Must contain only alphanumeric characters")
        String name) {

    private final static String NAME_PATTERN = "^[A-Za-z0-9]+( [A-Za-z0-9]+)*$";
}
