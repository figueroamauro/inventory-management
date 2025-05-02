package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

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

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable,
                                              PagedResourcesAssembler<ProductResponseDTO> assembler) {
        Page<Product> page = productService.findAll(pageable);
        Page<ProductResponseDTO> result = page.map(product -> {
            return new ProductResponseDTO(
                    product.getId(), product.getName(), product.getDescription(),
                    product.getPrice(), product.getCategory().getId(), product.getCreatedAt());
        });
        return ResponseEntity.ok(assembler.toModel(result));
    }
}
