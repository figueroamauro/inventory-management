package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByNameAndWarehouseIdAndActiveTrue(String name, Long warehouseId);

    Page<Location> findAllByWarehouseIdAndActiveTrue(Pageable pageable, Long warehouseId);

    Optional<Location> findByIdAndWarehouseIdAndActiveTrue(Long id, Long warehouseId);

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.active = false WHERE l.id = :id AND l.warehouseId = :warehouseId")
    void deleteByIdAndWarehouseId(Long id, Long warehouseId);
}
