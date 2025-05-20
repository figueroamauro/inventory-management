package ar.com.old.ms_stock.dto;

import ar.com.old.ms_stock.enums.MovementType;

import java.time.LocalDateTime;

public record StockMovementResponseDTO(
        Long id,
        MovementType entryType,
        Integer quantity,
        Long productId,
        String productName,
        Integer stock,
        Integer beforeStock,
        Integer afterStock,
        String note,
        String location,
        LocalDateTime createAt
) {
}
