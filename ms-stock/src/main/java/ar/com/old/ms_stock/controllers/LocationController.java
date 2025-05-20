package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.dto.LocationResponseDTO;
import ar.com.old.ms_stock.dto.LocationStockDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.services.LocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationResponseDTO> create(@Valid @RequestBody LocationDTO dto) {
        Location location = locationService.create(dto);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.created(URI.create("api/locations/" + location.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable, PagedResourcesAssembler<LocationResponseDTO> assembler) {
        Page<Location> page = locationService.findAll(pageable);

        Page<LocationResponseDTO> dtoPage = mapToLocationResponseDTOPage(page);

        return ResponseEntity.ok(assembler.toModel(dtoPage));
    }


    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> findOne(@PathVariable Long id) {
        Location location = locationService.findOne(id);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<LocationResponseDTO> update(@Valid @RequestBody LocationDTO dto) {
        Location location = locationService.update(dto);

        LocationResponseDTO response = buildResponse(location);

        return ResponseEntity.ok(response);
    }

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
