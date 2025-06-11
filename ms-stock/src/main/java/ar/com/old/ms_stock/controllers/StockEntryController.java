package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.services.StockEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stock", description = "Gestión de stock: consulta de información")
@RestController
@RequestMapping("/api/stock")
public class StockEntryController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10}";
    private final StockEntryService stockEntryService;

    public StockEntryController(StockEntryService stockEntryService) {
        this.stockEntryService = stockEntryService;
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de stock")
    @GetMapping
    public ResponseEntity<?> findAll(@Schema(example = DEFAULT_PAGE) Pageable pageable, PagedResourcesAssembler<StockEntry> assembler) {
        Page<StockEntry> page = stockEntryService.findAll(pageable);

        return ResponseEntity.ok(assembler.toModel(page));
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener stock por su id")
    @GetMapping("/{id}")
    public ResponseEntity<StockEntry> findOne(@PathVariable Long id) {
        StockEntry stockEntry = stockEntryService.findOne(id);

        return ResponseEntity.ok(stockEntry);
    }

}
