package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.dto.ProductUpdateDTO;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Productos", description = "Gestión de productos: creación, actualización, eliminación y consulta de información")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"name\"}";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiResponse(responseCode = "201")
    @Operation(summary = "Crear productos")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductDTO dto) {
        Product product = productService.create(dto);
        ProductResponseDTO responseDTO = toResponseDTO(product);

        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(responseDTO);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de productos")
    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault   @Schema(example = DEFAULT_PAGE) Pageable pageable,
                                     PagedResourcesAssembler<ProductResponseDTO> assembler) {

        Page<Product> page = productService.findAll(pageable);
        Page<ProductResponseDTO> result = page.map(this::toResponseDTO);

        return ResponseEntity.ok(assembler.toModel(result));
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener producto por su id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findOne(@PathVariable Long id) {
        Product product = productService.findOne(id);
        ProductResponseDTO response = toResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Actualizar producto")
    @PutMapping
    public ResponseEntity<ProductResponseDTO> update(@Valid @RequestBody ProductUpdateDTO dto) {
        Product product = productService.update(dto);
        ProductResponseDTO response = toResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "204")
    @Operation(summary = "Eliminar producto por su id")
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
