package ar.com.old.ms_stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StockMovementDTO(
        @NotNull
        @Pattern(regexp = "IN|OUT|RETURN", message = "Invalid movement type. Must be IN, OUT or RETURN")
        String type,
        @NotNull(message = "Quantity can not be null")
        Integer quantity,
        @Size(min = 0, max = 15, message = "Note must contain between 0 and 255 characters")
        String note,
        @NotNull(message = "Location id can not be null")
        Long locationId,
        @NotNull(message = "Product id can not be null")
        Long productId

) {
}
