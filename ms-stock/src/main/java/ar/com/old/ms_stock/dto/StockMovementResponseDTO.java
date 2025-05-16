package ar.com.old.ms_stock.dto;

import ar.com.old.ms_stock.enums.MovementType;

import java.time.LocalDateTime;

public record StockMovementResponseDTO(
        Long id,
        MovementType type,
        Integer quantity,
        Integer beforeStock,
        Integer afterStock,
        Integer currentStock,
        String note,
        LocalDateTime createAt,
        String locationName
) {
}
