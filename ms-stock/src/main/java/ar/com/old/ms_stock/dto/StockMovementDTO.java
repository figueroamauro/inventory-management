package ar.com.old.ms_stock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StockMovementDTO(
        @Schema(example = "IN|OUT|RETURN")
        @NotNull
        @Pattern(regexp = "IN|OUT|RETURN", message = "Invalid movement type. Must be IN, OUT or RETURN")
        String type,

        @Schema(example = "100")
        @NotNull(message = "Quantity can not be null")
        Integer quantity,

        @Schema(example = "Devolución del producto X")
        @Size(min = 0, max = 15, message = "Note must contain between 0 and 255 characters")
        String note,

        @Schema(example = "1")
        @NotNull(message = "Location id can not be null")
        Long locationId,

        @Schema(example = "1")
        @NotNull(message = "Product id can not be null")
        Long productId

) {
}
