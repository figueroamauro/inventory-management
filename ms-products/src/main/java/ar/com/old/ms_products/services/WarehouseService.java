package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface WarehouseService {

    Warehouse create(WarehouseDTO dto);

    Page<Warehouse> findAll(Pageable pageable);

    Warehouse findOne(Long id);

    Warehouse update(WarehouseDTO dto);

    void delete(Long id);
}
