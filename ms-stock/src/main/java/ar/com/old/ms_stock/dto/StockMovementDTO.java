package ar.com.old.ms_stock.dto;

import ar.com.old.ms_stock.enums.MovementType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StockMovementDTO(
        @Pattern(regexp = "^[A-Z_]+$", message = "Invalid type. Only uppercase letters and underscores are allowed")
        @Size(min = 2, max = 15, message = "Type must contain between 2 and 15 characters")
        @NotNull
        MovementType type,
        @NotNull(message = "Quantity can not be null")
        Integer quantity,
        @Max(value = 255)
        String note,
        @NotNull(message = "Location id can not be null")
        Long locationId,
        @NotNull(message = "Product id can not be null")
        Long productId

) {
}
