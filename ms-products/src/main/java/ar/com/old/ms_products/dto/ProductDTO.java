package ar.com.old.ms_products.dto;

import jakarta.validation.constraints.*;

public record ProductDTO(
        @NotBlank(message = "Product can not be blank")
        @Size(min = 4, max = 30, message = "Product must be between 4 and 30 characters long")
        @Pattern(regexp = NAME_PATTERN, message = "Invalid product name pattern. Must contain only alphanumeric characters")
        String name,

        @NotBlank(message = "Description can not be blank")
        @Size(min = 4, max = 200, message = "Product must be between 4 and 200 characters long")
        String description,

        @Min(value = 0, message = "Price must be longer than 0")
        @Max(value = 50000000, message = "Price must be less than 50.000.000")
        @NotNull(message = "Price can not be null")
        Double price,

        @NotNull(message = "Category id can not be null")
        Long categoryId
) {
    private final static String NAME_PATTERN = "^[A-Za-z0-9]+( [A-Za-z0-9]+)*$";
}

