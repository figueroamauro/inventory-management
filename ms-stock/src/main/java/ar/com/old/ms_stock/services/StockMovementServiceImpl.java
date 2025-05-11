package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
import org.springframework.data.domain.Page;

import java.util.Optional;

public class StockMovementServiceImpl implements StockMovementService{
    private final StockMovementRepository stockMovementRepository;
    private final StockEntryRepository stockEntryRepository;
    private final WarehouseClientService clientService;
    private final LocationRepository locationRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, StockEntryRepository stockEntryRepository, WarehouseClientService clientService, LocationRepository locationRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockEntryRepository = stockEntryRepository;
        this.clientService = clientService;
        this.locationRepository = locationRepository;
    }

    @Override
    public StockMovement create(StockMovementDTO dto) {
        validateNull(dto, "DTO can not be null");
        WarehouseDTO warehouse = clientService.getWarehouse();
        Location location = locationRepository.findByIdAndWarehouseId(dto.locationId(), warehouse.id())
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        StockEntry entry = stockEntryRepository.findByIdAndWarehouseId(dto.productId(), warehouse.id())
                .orElseGet(() -> stockEntryRepository.save(new StockEntry(dto.quantity(), dto.productId(), warehouse.id())));

        StockMovement stockMovement = new StockMovement(null, dto.type(), dto.quantity(), dto.note(), location, entry);
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public Page<StockMovement> findAllByStockEntry_WarehouseId(Long warehouseId) {
        return null;
    }

    @Override
    public Page<StockMovement> findAllByStockEntry_ProductId(Long productId) {
        return null;
    }


    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
