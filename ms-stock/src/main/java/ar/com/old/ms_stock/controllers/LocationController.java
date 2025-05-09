package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.services.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody LocationDTO dto) {
        Location location = locationService.create(dto);

        return ResponseEntity.created(URI.create("api/locations/" + location.getId())).body(location);
    }

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable, PagedResourcesAssembler<Location> assembler) {
        Page<Location> page = locationService.findAll(pageable);

        return ResponseEntity.ok(assembler.toModel(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> findOne(@PathVariable Long id) {
        Location location = locationService.findOne(id);

        return ResponseEntity.ok(location);
    }
}
