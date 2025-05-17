package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.LocationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationStockRepository extends JpaRepository<LocationStock,Long> {
}
