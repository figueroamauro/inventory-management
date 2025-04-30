package ar.com.old.ms_products.dto;

public record ProductUpdateDTO(
        Long id,
        String name,
        String description,
        Double price,
        Long categoryId

) {
}
