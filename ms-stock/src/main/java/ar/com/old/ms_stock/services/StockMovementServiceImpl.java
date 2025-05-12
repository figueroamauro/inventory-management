package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.exceptions.ProductNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class StockMovementServiceImpl implements StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final StockEntryRepository stockEntryRepository;
    private final ProductsClientService productsClientService;
    private final LocationRepository locationRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    StockEntryRepository stockEntryRepository,
                                    ProductsClientService clientService,
                                    LocationRepository locationRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockEntryRepository = stockEntryRepository;
        this.productsClientService = clientService;
        this.locationRepository = locationRepository;
    }

    @Override
    public StockMovement create(StockMovementDTO dto) {
        validateNull(dto, "DTO can not be null");
        validateNonExistentProduct(dto.productId());

        WarehouseDTO warehouse = productsClientService.getWarehouse();

        Location location = getLocationAndVerifyIfExist(dto, warehouse);

        StockEntry entry = getEntryAndPersistIfNotExists(dto, warehouse);

        StockMovement stockMovement = new StockMovement(null, dto.type(), dto.quantity(), dto.note(), location, entry);

        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public Page<StockMovement> findAll(Pageable pageable) {
        WarehouseDTO warehouse = productsClientService.getWarehouse();

        return stockMovementRepository.findAllByStockEntry_WarehouseId(pageable, warehouse.id());
    }

    @Override
    public Page<StockMovement> findAllByProductId(Pageable pageable, Long productId) {
        return null;
    }


    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNonExistentProduct(Long id) {
        ProductDTO product = productsClientService.getProduct(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found");
        }
    }

    private Location getLocationAndVerifyIfExist(StockMovementDTO dto, WarehouseDTO warehouse) {
        return locationRepository.findByIdAndWarehouseId(dto.locationId(), warehouse.id())
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
    }

    private StockEntry getEntryAndPersistIfNotExists(StockMovementDTO dto, WarehouseDTO warehouse) {
        return stockEntryRepository.findByIdAndWarehouseId(dto.productId(), warehouse.id())
                .orElseGet(() -> stockEntryRepository.save(new StockEntry(dto.quantity(), dto.productId(), warehouse.id())));
    }
}
