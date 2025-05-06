package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    @Override
    public Page<Location> findAll(Pageable pageable) {
        WarehouseDTO warehouse = clientService.getWarehouse();
        return locationRepository.findAllByWarehouseId(pageable, warehouse.id());
    }

    @Override
    public Location findOne(Long id) {
        validateNull(id, "Id can not be null");

        WarehouseDTO warehouse = clientService.getWarehouse();

        return locationRepository.findByIdAndWarehouseId(id, warehouse.id())
                .orElseThrow(()-> new LocationNotFoundException("Location not found"));
    }

    @Override
    public Location update(LocationDTO dto) {
        WarehouseDTO warehouse = clientService.getWarehouse();

        Location location = locationRepository.findByIdAndWarehouseId(dto.id(), warehouse.id())
                .orElseThrow(()-> new LocationNotFoundException("Location not found"));

        location.setId(dto.id());
        location.setName(dto.name());

        return locationRepository.save(location);
    }

    @Override
    public void delete(Long id) {

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
}
