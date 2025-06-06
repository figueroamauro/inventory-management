package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.services.WarehouseService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Almacenes", description = "Gestión de almacenes: creación, actualización  y consulta de información")
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"name\"}";

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @ApiResponse(responseCode = "201")
    @Operation(summary = "Crear almacen")
    @PostMapping
    public ResponseEntity<Warehouse> create(@Valid @RequestBody WarehouseDTO dto) {
        Warehouse warehouse = warehouseService.create(dto);
        return ResponseEntity.created(URI.create("/api/warehouses/" + warehouse.getId())).body(warehouse);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de almacenes")
    @GetMapping
    public ResponseEntity<PagedModel<?>> findAll(@PageableDefault(sort = "name")
                                                 @Schema(example = DEFAULT_PAGE)
                                                 Pageable pageable
            , PagedResourcesAssembler<Warehouse> assembler) {
        Page<Warehouse> page = warehouseService.findAll(pageable);
        return ResponseEntity.ok(assembler.toModel(page));
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener un almacen por su id")
    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> findOne(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findOne(id);
        return ResponseEntity.ok(warehouse);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Actualizar almacen")
    @PutMapping
    public ResponseEntity<Warehouse> update(@Valid @RequestBody WarehouseDTO dto) {
        Warehouse warehouse = warehouseService.update(dto);
        return ResponseEntity.ok(warehouse);
    }


    @Hidden
    @GetMapping("/current")
    public ResponseEntity<Warehouse> findCurrent() {
        Warehouse warehouse = warehouseService.findCurrentWarehouse();
        return ResponseEntity.ok(warehouse);
    }
}
