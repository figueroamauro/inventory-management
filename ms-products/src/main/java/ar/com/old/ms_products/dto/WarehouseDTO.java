package ar.com.old.ms_products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WarehouseDTO(
        Long id,

        @NotBlank(message = "Warehouse name can not be blank")
        @Size(min = 4, max = 30, message = "Warehouse name must be between 4 and 30 characters long")
        @Pattern(regexp = NAME_PATTERN,message = "Invalid warehouse name pattern. Must contain only alphanumeric characters")
        String name
) {
    private final static String NAME_PATTERN = "^[A-Za-z0-9]+( [A-Za-z0-9]+)*$";
}
