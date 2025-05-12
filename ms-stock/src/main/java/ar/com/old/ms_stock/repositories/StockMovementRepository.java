package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    boolean existsByLocationId(Long id);

    Page<StockMovement> findAllByStockEntry_WarehouseId(Pageable pageable, Long warehouseId);

    Page<StockMovement> findAllByStockEntry_ProductId(Pageable any, Long productId);

    Page<StockMovement> findAllByLocationId(Pageable pageable, Long locationId);

    Page<StockMovement> findAllByLocationIdAndStockEntry_ProductId(Pageable pageable, Long locationId, Long productId);
}
