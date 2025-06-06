package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.dto.LocationResponseDTO;
import ar.com.old.ms_stock.dto.LocationStockDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Ubicaciones", description = "Gestión de ubicaciones: creación, actualización, eliminación y consulta de información")
@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"name\"}";

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @ApiResponse(responseCode = "201")
    @Operation(summary = "Crear ubicaciones")
    @PostMapping
    public ResponseEntity<LocationResponseDTO> create(@Valid @RequestBody LocationDTO dto) {
        Location location = locationService.create(dto);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.created(URI.create("api/locations/" + location.getId())).body(response);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de ubicaciones")
    @GetMapping
    public ResponseEntity<?> findAll(@Schema(example = DEFAULT_PAGE) Pageable pageable, PagedResourcesAssembler<LocationResponseDTO> assembler) {
        Page<Location> page = locationService.findAll(pageable);

        Page<LocationResponseDTO> dtoPage = mapToLocationResponseDTOPage(page);

        return ResponseEntity.ok(assembler.toModel(dtoPage));
    }


    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener ubicaciones por su id")
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> findOne(@PathVariable Long id) {
        Location location = locationService.findOne(id);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Actualizar ubicacion")
    @PutMapping
    public ResponseEntity<LocationResponseDTO> update(@Valid @RequestBody LocationDTO dto) {
        Location location = locationService.update(dto);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.ok(response);
    }


    @ApiResponse(responseCode = "204")
    @Operation(summary = "Eliminar ubicacion por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        locationService.delete(id);

        return ResponseEntity.noContent().build();
    }


    private LocationResponseDTO buildResponse(Location location) {
        List<LocationStockDTO> list = getLocationResponseDTOList(location);
        return new LocationResponseDTO(location.getId(), location.getName(), list);
    }

    private static Page<LocationResponseDTO> mapToLocationResponseDTOPage(Page<Location> page) {
        return page.map(location -> {
            List<LocationStockDTO> stockList = location.getLocationStockList().stream()
                    .map(stock -> new LocationStockDTO(stock.getId(), stock.getProductId(), stock.getQuantity()))
                    .toList();

            return new LocationResponseDTO(location.getId(), location.getName(), stockList);
        });
    }

    private List<LocationStockDTO> getLocationResponseDTOList(Location location) {

        return location.getLocationStockList().stream().map(stock ->
                new LocationStockDTO(stock.getId(), stock.getProductId(), stock.getQuantity())).toList();
    }
}
