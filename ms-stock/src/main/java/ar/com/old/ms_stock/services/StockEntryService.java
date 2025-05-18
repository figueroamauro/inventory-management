package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.entities.StockEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface StockEntryService {
    Page<StockEntry> findAll(Pageable pageable);

    StockEntry findOne(Long id);
}
