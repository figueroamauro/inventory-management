package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.dto.StockMovementResponseDTO;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/movements")
public class StockMovementController {

    private final StockMovementService movementService;
    private final ProductsClientService productsClientService;

    public StockMovementController(StockMovementService movementService, ProductsClientService productsClientService) {
        this.movementService = movementService;
        this.productsClientService = productsClientService;
    }

    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> create(@Valid @RequestBody StockMovementDTO dto) {
        StockMovement stockMovement = movementService.create(dto);

        ProductDTO product = productsClientService.getProduct(dto.productId());

        StockMovementResponseDTO response = new StockMovementResponseDTO(stockMovement.getId(), stockMovement.getType(),
                stockMovement.getQuantity(),product.id(), product.name(),stockMovement.getStockEntry().getQuantity(),
                stockMovement.getBeforeStock(), stockMovement.getAfterStock(), stockMovement.getNote(),
                stockMovement.getLocation().getName(),stockMovement.getCreateAt());

        return ResponseEntity.created(URI.create("/api/movements/" + stockMovement.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                     PagedResourcesAssembler<StockMovementResponseDTO> assembler,
                                     @RequestParam(required = false) Long locationId,
                                     @RequestParam(required = false) Long productId) {


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

        Page<StockMovementResponseDTO> result = page.map(stockMovement -> {
            ProductDTO product = productsClientService.getProduct(stockMovement.getStockEntry().getProductId());

            return new StockMovementResponseDTO(stockMovement.getId(), stockMovement.getType(),
                    stockMovement.getQuantity(),product.id(), product.name(),stockMovement.getStockEntry().getQuantity(),
                    stockMovement.getBeforeStock(), stockMovement.getAfterStock(), stockMovement.getNote(),
                    stockMovement.getLocation().getName(),stockMovement.getCreateAt());
        });

        return ResponseEntity.ok(assembler.toModel(result));
    }


}
