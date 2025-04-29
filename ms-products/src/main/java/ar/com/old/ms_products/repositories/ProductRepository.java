package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByNameAndWarehouseId(String name, Long warehouseId);
}
