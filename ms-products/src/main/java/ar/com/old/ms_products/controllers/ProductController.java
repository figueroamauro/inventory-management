package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody ProductDTO dto) {
        Product product = productService.create(dto);
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getCategory().getId(), product.getCreatedAt());

        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(responseDTO);
    }
}
