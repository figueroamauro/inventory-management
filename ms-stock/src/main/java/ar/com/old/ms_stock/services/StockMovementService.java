package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface StockMovementService {

    StockMovement create(StockMovementDTO dto);

    Page<StockMovement> findAllByStockEntry_WarehouseId(Pageable pageable,Long warehouseId);

    Page<StockMovement> findAllByStockEntry_ProductId(Pageable pageable, Long productId);
}
