package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.services.StockEntryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock")
public class StockEntryController {
    private final StockEntryService stockEntryService;

    public StockEntryController(StockEntryService stockEntryService) {
        this.stockEntryService = stockEntryService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable, PagedResourcesAssembler<StockEntry> assembler) {
        Page<StockEntry> page = stockEntryService.findAll(pageable);

        return ResponseEntity.ok(assembler.toModel(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockEntry> findOne(@PathVariable Long id) {
        StockEntry stockEntry = stockEntryService.findOne(id);

        return ResponseEntity.ok(stockEntry);
    }

}
