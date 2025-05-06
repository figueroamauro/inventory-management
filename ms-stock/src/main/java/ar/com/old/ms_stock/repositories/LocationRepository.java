package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByNameAndWarehouseId(String name, Long warehouseId);

    Page<Location> findAllByWarehouseId(Pageable pageable, Long warehouseId);
}
