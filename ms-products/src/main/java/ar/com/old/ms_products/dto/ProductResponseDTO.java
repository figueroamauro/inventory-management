package ar.com.old.ms_products.dto;

import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        Double price,
        Long category_id,
        LocalDateTime created_at
) {
}
