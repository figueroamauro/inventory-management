package ar.com.old.ms_stock.dto;

import ar.com.old.ms_stock.enums.MovementType;

public record StockMovementDTO(
        MovementType type,
        Integer quantity,
        String note,
        Long locationId,
        Long productId

) {
}
