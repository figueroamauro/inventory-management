package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StockEntryServiceImpl implements StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductsClientService productsClientService;

    public StockEntryServiceImpl(StockEntryRepository stockEntryRepository, ProductsClientService productsClientService) {
        this.stockEntryRepository = stockEntryRepository;
        this.productsClientService = productsClientService;
    }


    @Override
    public Page<StockEntry> findAll(Pageable pageable) {

        WarehouseDTO warehouse = productsClientService.getWarehouse();
        return stockEntryRepository.findAllByWarehouseId(pageable, warehouse.id());

    }

    @Override
    public StockEntry findOne(Long id) {
        return null;
    }
}
