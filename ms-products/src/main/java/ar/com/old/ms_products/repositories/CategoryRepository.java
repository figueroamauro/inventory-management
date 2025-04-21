package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndWarehouseId(String name, Long warehouseId);

    Optional<Category> findByIdAndWarehouseId(Long id, Long warehouseId);
}
