package ar.com.old.ms_products.dto;

public record ProductDTO(
        String name,
        String description,
        Double price,
        Long categoryId
        ) {
}
