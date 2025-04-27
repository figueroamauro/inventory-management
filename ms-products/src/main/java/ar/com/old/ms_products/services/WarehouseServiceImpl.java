package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.WarehouseAlreadyExistException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class WarehouseServiceImpl implements WarehouseService {

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
       return warehouseRepository.findByIdAndUserId(id, userDTO.id())
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
    }

    @Override
    @Transactional
    public Warehouse create(WarehouseDTO dto) {
        validateNull(dto, "You must provide a valid request body");

        UserDTO userDTO = clientService.getUser();
        hasWarehouse(userDTO);
        Warehouse warehouse = new Warehouse(dto.id(), dto.name(), userDTO.id());
        verifyExistentWarehouse(warehouse);

        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse update(WarehouseDTO dto) {
        validateNull(dto,"You must provide a valid request body");
        validateNull(dto.id(),"Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = warehouseRepository.findByIdAndUserId(dto.id(), userDTO.id())
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));

        warehouse.setName(dto.name());

        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        validateNull(id,"Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = warehouseRepository.findByIdAndUserId(id, userDTO.id())
                .orElseThrow();

        warehouseRepository.deleteById(id);
    }


    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void verifyExistentWarehouse(Warehouse warehouse) {
        Optional<Warehouse> warehouseOpt = warehouseRepository
                .findByNameAndUserId(warehouse.getName(),warehouse.getUserId());
        if (warehouseOpt.isPresent()) {
            throw new WarehouseAlreadyExistException("Warehouse already exist");
        }
    }

    private void hasWarehouse(UserDTO userDTO) {
        List<Warehouse> list = warehouseRepository.findAll();
        if (!list.isEmpty()) {
            throw new WarehouseAlreadyExistException("you already have a registered warehouse");
        }
    }

}
