package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.services.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/movements")
public class StockMovementController {

    private final StockMovementService movementService;

    public StockMovementController(StockMovementService movementService) {
        this.movementService = movementService;
    }

    @PostMapping
    public ResponseEntity<StockMovement> create(@RequestBody StockMovementDTO dto) {
        StockMovement stockMovement = movementService.create(dto);

        return ResponseEntity.created(URI.create("/api/movements/" + stockMovement.getId())).body(stockMovement);
    }


}
