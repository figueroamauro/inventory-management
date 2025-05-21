package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.exceptions.LocationInUseException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final ProductsClientService clientService;

    public LocationServiceImpl(LocationRepository locationRepository, ProductsClientService clientService) {
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
        return locationRepository.findAllByWarehouseIdAndActiveTrue(pageable, warehouse.id());
    }

    @Override
    public Location findOne(Long id) {
        validateNull(id, "Id can not be null");

        WarehouseDTO warehouse = clientService.getWarehouse();

        return locationRepository.findByIdAndWarehouseIdAndActiveTrue(id, warehouse.id())
                .orElseThrow(()-> new LocationNotFoundException("Location not found"));
    }

    @Override
    public Location update(LocationDTO dto) {
        validateNull(dto, "DTO can not be null");
        validateNull(dto.id(), "Id can not be null");

        WarehouseDTO warehouse = clientService.getWarehouse();

        validateExistingLocation(dto.name(),warehouse.id());

        Location location = locationRepository.findByIdAndWarehouseIdAndActiveTrue(dto.id(), warehouse.id())
                .orElseThrow(()-> new LocationNotFoundException("Location not found"));

        location.setId(dto.id());
        location.setName(dto.name());

        return locationRepository.save(location);
    }

    @Override
    public void delete(Long id) {
        validateNull(id, "Id can not be null");
        validateLocationIsDeletable(id);

        WarehouseDTO warehouse = clientService.getWarehouse();

        locationRepository.deleteByIdAndWarehouseId(id, warehouse.id());
    }




    private static void validateNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateExistingLocation(String name, Long id) {
        Optional<Location> locationOpt = locationRepository.findByNameAndWarehouseIdAndActiveTrue(name, id);
        if (locationOpt.isPresent()) {
            throw new LocationAlreadyExistException("Location already exist");
        }
    }

    private void validateLocationIsDeletable(Long id) {
        if (hasStock(id)) {
            throw new LocationInUseException("Location is in use and cannot be deleted");
        }
    }


    private boolean hasStock(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
        return !location.getLocationStockList().isEmpty();

    }


}
