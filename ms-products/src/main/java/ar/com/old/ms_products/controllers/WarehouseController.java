package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.services.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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

    @GetMapping
    public ResponseEntity<PagedModel<?>> findAll(@PageableDefault(sort = "name") Pageable pageable, PagedResourcesAssembler<Warehouse> assembler) {
        Page<Warehouse> page = warehouseService.findAll(pageable);
        return ResponseEntity.ok(assembler.toModel(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> findOne(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findOne(id);
        return ResponseEntity.ok(warehouse);
    }

}
