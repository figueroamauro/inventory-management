package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    boolean existsByLocationId(Long id);
}
