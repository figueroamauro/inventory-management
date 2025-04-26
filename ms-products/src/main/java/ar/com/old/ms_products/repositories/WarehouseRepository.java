package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByUserId(Long id);

    Optional<Warehouse> findByNameAndUserId(String name, Long UserId);

    Optional<Warehouse> findByIdAndUserId(Long id, Long userId);
}
