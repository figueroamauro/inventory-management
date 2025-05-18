package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.LocationStock;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.exceptions.LocationConflictException;
import ar.com.old.ms_stock.exceptions.NegativeStockException;
import ar.com.old.ms_stock.exceptions.ProductConflictException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
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
    @Transactional
    public StockMovement create(StockMovementDTO dto) {
        validateNull(dto, "DTO can not be null");
        validateNonExistentProduct(dto.productId());

        WarehouseDTO warehouse = productsClientService.getWarehouse();
        Location location = getLocationAndVerifyIfExist(dto, warehouse);
        StockEntry entry = getEntryAndPersistIfNotExists(dto, warehouse);
        entry.setUpdateAt(LocalDateTime.now());

        StockMovement stockMovement = new StockMovement(null, MovementType.valueOf(dto.type()), dto.quantity(), entry.getQuantity(), null, dto.note(), location, entry);

        adjustLocationStock(dto, location);
        adjustStock(entry, dto);
        stockMovement.setAfterStock(entry.getQuantity());

        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public Page<StockMovement> findAll(Pageable pageable) {
        WarehouseDTO warehouse = productsClientService.getWarehouse();

        return stockMovementRepository.findAllByStockEntry_WarehouseId(pageable, warehouse.id());
    }

    @Override
    public Page<StockMovement> findAllByProductId(Pageable pageable, Long productId) {
        validateNull(productId, "Id can not be null");

        return stockMovementRepository.findAllByStockEntry_ProductId(pageable, productId);
    }

    @Override
    public Page<StockMovement> findAllByLocationId(Pageable pageable, Long locationId) {
        validateNull(locationId, "Id can not be null");

        return stockMovementRepository.findAllByLocationId(pageable, locationId);
    }

    @Override
    public Page<StockMovement> findAllByLocationIdAndProductId(Pageable pageable, Long locationId, Long productId) {
        validateNull(locationId, "Location id can not be null");
        validateNull(productId, "Product id can not be null");

        return stockMovementRepository.findAllByLocationIdAndStockEntry_ProductId(pageable, locationId, productId);
    }


    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNonExistentProduct(Long id) {
        ProductDTO product = productsClientService.getProduct(id);
        if (product == null) {
            throw new ProductConflictException("Product not found");
        }
    }

    private Location getLocationAndVerifyIfExist(StockMovementDTO dto, WarehouseDTO warehouse) {
        return locationRepository.findByIdAndWarehouseIdAndActiveTrue(dto.locationId(), warehouse.id())
                .orElseThrow(() -> new LocationConflictException("Location not found"));
    }

    private StockEntry getEntryAndPersistIfNotExists(StockMovementDTO dto, WarehouseDTO warehouse) {
        return stockEntryRepository.findByIdAndWarehouseId(dto.productId(), warehouse.id())
                .orElseGet(() -> stockEntryRepository.save(new StockEntry(0, dto.productId(), warehouse.id())));
    }

    private void adjustStock(StockEntry entry, StockMovementDTO dto) {
        int quantity = dto.quantity();
        MovementType type = MovementType.valueOf(dto.type());

        switch (type) {
            case IN:
                entry.setQuantity(entry.getQuantity() + quantity);
                break;

            case OUT:
            case RETURN:
                int newQuantity = entry.getQuantity() - quantity;
                if (newQuantity < 0) {
                    throw new NegativeStockException("Stock can not be negative for productName ID: " + dto.productId());
                }
                entry.setQuantity(newQuantity);
                break;

            default:
                throw new IllegalArgumentException("Unsupported movement type: " + type);
        }
    }

    private void adjustLocationStock(StockMovementDTO dto, Location location) {

        LocationStock locationStock = getLocationStockOrCreateIfNotExist(dto, location);

        if (dto.type().equals("IN")) {
            locationStock.setQuantity(locationStock.getQuantity() + dto.quantity());
        } else if (dto.type().equals("OUT") || dto.type().equals("RETURN")) {
            int updated = locationStock.getQuantity() - dto.quantity();
            if (updated < 0) {
                throw new NegativeStockException("There is not enough stock of the product at this location.");
            } else if (updated == 0) {
                location.removeStock(locationStock);
                return;
            }
            locationStock.setQuantity(updated);
        }
    }

    private LocationStock getLocationStockOrCreateIfNotExist(StockMovementDTO dto, Location location) {
        return location.getLocationStockList().stream().filter(stock -> stock.getProductId().equals(dto.productId()))
                .findFirst()
                .orElseGet(() -> {
                    LocationStock newStock = new LocationStock(null, dto.productId(), 0, location);
                    location.addStock(newStock);
                    return newStock;
                });
    }

}
