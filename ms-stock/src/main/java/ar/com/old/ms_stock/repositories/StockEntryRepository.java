package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.StockEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {

    Optional<StockEntry> findByIdAndWarehouseId(Long id, Long WarehouseId);

    Page<StockEntry> findAllByWarehouseId(Pageable pageable, Long warehouseId);

    Page<StockEntry> findAllByProductId(Pageable any, Long productId);

}
