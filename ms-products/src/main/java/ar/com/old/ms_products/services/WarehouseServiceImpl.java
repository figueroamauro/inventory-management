package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class WarehouseServiceImpl implements WarehouseService{

    private final WarehouseRepository warehouseRepository;
    private final UserClientService clientService;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, UserClientService clientService) {
        this.warehouseRepository = warehouseRepository;
        this.clientService = clientService;
    }

    @Override
    public Page<Warehouse> findAll(Pageable pageable) {
    validateNull(pageable, "Pageable can not be null");
        return warehouseRepository.findAll(pageable);
    }

    @Override
    public Warehouse findOne(Long id) {
     validateNull(id, "Id can not be null");
        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse= warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        return validateWarehouseOwner(warehouse, userDTO);
    }


    @Override
    public Warehouse create(WarehouseDTO dto) {
        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = new Warehouse(dto.id(), dto.name(), userDTO.id());
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse update(WarehouseDTO dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static Warehouse validateWarehouseOwner(Warehouse warehouse, UserDTO userDTO) {
        if (warehouse.getUserId().equals(userDTO.id())) {
            return warehouse;
        } else {
            throw new WarehouseNotFoundException("Warehouse not found");
        }
    }
}
