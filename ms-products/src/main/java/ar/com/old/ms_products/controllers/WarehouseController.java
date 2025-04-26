package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.services.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public ResponseEntity<Warehouse> create(@RequestBody WarehouseDTO dto) {
        Warehouse warehouse = warehouseService.create(dto);
        return ResponseEntity.created(URI.create("/api/warehouses/" + warehouse.getId())).body(warehouse);
    }

}
