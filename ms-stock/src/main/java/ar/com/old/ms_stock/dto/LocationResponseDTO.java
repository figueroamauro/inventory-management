package ar.com.old.ms_stock.dto;

import java.util.List;

public record LocationResponseDTO(
        Long id,
        String name,
        List<LocationStockDTO> products

) {
}
