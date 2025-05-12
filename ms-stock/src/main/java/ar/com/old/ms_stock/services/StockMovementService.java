package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface StockMovementService {

    StockMovement create(StockMovementDTO dto);

    Page<StockMovement> findAll(Pageable pageable);

    Page<StockMovement> findAllByProductId(Pageable pageable, Long productId);
}
