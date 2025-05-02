package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.dto.ProductUpdateDTO;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductDTO dto) {
        Product product = productService.create(dto);
        ProductResponseDTO responseDTO = toResponseDTO(product);

        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault Pageable pageable,
                                     PagedResourcesAssembler<ProductResponseDTO> assembler) {

        Page<Product> page = productService.findAll(pageable);
        Page<ProductResponseDTO> result = page.map(this::toResponseDTO);

        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findOne(@PathVariable Long id) {
        Product product = productService.findOne(id);
        ProductResponseDTO response = toResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ProductResponseDTO> update(@Valid @RequestBody ProductUpdateDTO dto) {
        Product product = productService.update(dto);
        ProductResponseDTO response = toResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponseDTO toResponseDTO(Product product) {

        return new ProductResponseDTO(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getCategory().getId(), product.getCreatedAt());
    }
}
