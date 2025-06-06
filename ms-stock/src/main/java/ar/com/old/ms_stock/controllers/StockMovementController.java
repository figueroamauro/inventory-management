package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.dto.StockMovementResponseDTO;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.services.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Movimientos de stock", description = "Gestión de movimientos: creación y consulta de información")
@RestController
@RequestMapping("/api/movements")
public class StockMovementController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"name\"}";

    private final StockMovementService movementService;
    private final ProductsClientService productsClientService;

    public StockMovementController(StockMovementService movementService, ProductsClientService productsClientService) {
        this.movementService = movementService;
        this.productsClientService = productsClientService;
    }

    @ApiResponse(responseCode = "201")
    @Operation(summary = "Crear movimiento")
    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> create(@Valid @RequestBody StockMovementDTO dto) {
        StockMovement stockMovement = movementService.create(dto);

        ProductDTO product = productsClientService.getProduct(dto.productId());

        StockMovementResponseDTO response = getStockMovementResponseDTO(stockMovement, product);

        return ResponseEntity.created(URI.create("/api/movements/" + stockMovement.getId())).body(response);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de movimientos")
    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                     @Schema(example = DEFAULT_PAGE)
                                     Pageable pageable,
                                     PagedResourcesAssembler<StockMovementResponseDTO> assembler,
                                     @Parameter(in = ParameterIn.QUERY, description = "ID de la ubicacion", example = "1")
                                     @RequestParam(required = false) Long locationId,
                                     @Parameter(in = ParameterIn.QUERY, description = "ID del producto", example = "1")
                                     @RequestParam(required = false) Long productId) {

        Page<StockMovement> page = getPage(pageable, locationId, productId);

        Page<StockMovementResponseDTO> result = mapToStockMovementResponseDTO(page);

        return ResponseEntity.ok(assembler.toModel(result));
    }


    private static StockMovementResponseDTO getStockMovementResponseDTO(StockMovement stockMovement, ProductDTO product) {
        return new StockMovementResponseDTO(stockMovement.getId(), stockMovement.getType(),
                stockMovement.getQuantity(), product.id(), product.name(), stockMovement.getStockEntry().getQuantity(),
                stockMovement.getBeforeStock(), stockMovement.getAfterStock(), stockMovement.getNote(),
                stockMovement.getLocation().getName(), stockMovement.getCreateAt());
    }

    private Page<StockMovement> getPage(Pageable pageable, Long locationId, Long productId) {
        Page<StockMovement> page;

        if (locationId != null && productId != null) {
            page = movementService.findAllByLocationIdAndProductId(pageable, locationId, productId);
        } else if (locationId != null) {
            page = movementService.findAllByLocationId(pageable, locationId);
        } else if (productId != null) {
            page = movementService.findAllByProductId(pageable, productId);
        } else {
            page = movementService.findAll(pageable);
        }
        return page;
    }

    private Page<StockMovementResponseDTO> mapToStockMovementResponseDTO(Page<StockMovement> page) {
        return page.map(stockMovement -> {
            ProductDTO product = productsClientService.getProduct(stockMovement.getStockEntry().getProductId());

            return getStockMovementResponseDTO(stockMovement, product);
        });
    }
}
