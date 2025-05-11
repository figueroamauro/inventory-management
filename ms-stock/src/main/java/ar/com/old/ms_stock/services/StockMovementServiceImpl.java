package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
import org.springframework.data.domain.Page;

public class StockMovementServiceImpl implements StockMovementService{
    private final StockMovementRepository stockMovementRepository;
    private final StockEntryRepository stockEntryRepository;
    private final WarehouseClientService clientService;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, StockEntryRepository stockEntryRepository, WarehouseClientService clientService) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockEntryRepository = stockEntryRepository;
        this.clientService = clientService;
    }

    @Override
    public StockMovement create(StockMovementDTO dto) {
        WarehouseDTO warehouse = clientService.getWarehouse();
        StockEntry entry = stockEntryRepository.findByIdAndWarehouseId(dto.productId(), warehouse.id())
                .orElseGet(()-> stockEntryRepository.save(new StockEntry(dto.quantity(),dto.productId(),warehouse.id())));

        return null;
    }

    @Override
    public Page<StockMovement> findAllByStockEntry_WarehouseId(Long warehouseId) {
        return null;
    }

    @Override
    public Page<StockMovement> findAllByStockEntry_ProductId(Long productId) {
        return null;
    }
}
