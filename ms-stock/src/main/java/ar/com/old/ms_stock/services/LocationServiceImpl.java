package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final WarehouseClientService clientService;

    public LocationServiceImpl(LocationRepository locationRepository, WarehouseClientService clientService) {
        this.locationRepository = locationRepository;
        this.clientService = clientService;
    }

    @Override
    public Location create(LocationDTO dto) {
        validateNull(dto, "DTO can not be null");

        WarehouseDTO warehouse = clientService.getWarehouse();
        validateExistingLocation(dto.name(),warehouse.id());

        Location location = new Location(null, dto.name(), warehouse.id());

        return locationRepository.save(location);
    }

    private static void validateNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateExistingLocation(String name, Long id) {
        Optional<Location> locationOpt = locationRepository.findByNameAndWarehouseId(name, id);
        if (locationOpt.isPresent()) {
            throw new LocationAlreadyExistException("Location already exist");
        }
    }
    @Override
    public Page<Location> findAll() {
        return null;
    }

    @Override
    public Location findOne(Long id) {
        return null;
    }

    @Override
    public Location update(Location location) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
