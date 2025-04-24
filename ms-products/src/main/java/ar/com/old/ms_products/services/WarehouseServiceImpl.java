package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WarehouseServiceImpl implements WarehouseService{

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Page<Warehouse> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable can not be null");
        }
        return warehouseRepository.findAll(pageable);
    }

    @Override
    public Optional<Warehouse> findOne(Long id) {
        return Optional.empty();
    }

    @Override
    public Warehouse create(WarehouseDTO dto) {
        return null;
    }

    @Override
    public Warehouse update(WarehouseDTO dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
