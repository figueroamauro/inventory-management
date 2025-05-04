package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
}
