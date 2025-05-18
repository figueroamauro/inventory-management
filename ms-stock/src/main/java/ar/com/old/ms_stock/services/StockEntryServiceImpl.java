package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.entities.StockEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StockEntryServiceImpl implements StockEntryService {
    @Override
    public Page<StockEntry> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<StockEntry> findAllByProductId(Pageable pageable, Long productId) {
        return null;
    }

    @Override
    public Page<StockEntry> findAllByLocationId(Pageable pageable, Long locationId) {
        return null;
    }

    @Override
    public Page<StockEntry> findAllByLocationIdAndProductId(Pageable pageable, Long locationId, Long productId) {
        return null;
    }
}
