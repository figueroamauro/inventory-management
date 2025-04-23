package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface WarehouseService {

    Page<Warehouse> findAll(Pageable pageable);

    Optional<Warehouse> findOne(Long id);

    Warehouse create(WarehouseDTO dto);

    Warehouse update(WarehouseDTO dto);

    void delete(Long id);
}
