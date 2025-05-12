package ar.com.old.ms_stock.clients.dto;

import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        String name,
        String description,
        Double price,
        Long category_id,
        LocalDateTime created_at
) {
}
