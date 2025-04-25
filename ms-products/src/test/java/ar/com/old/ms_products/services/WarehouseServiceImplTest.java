package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private UserClientService clientService;


    @Test
    void shouldFindAllWarehouses(){
        //GIVEN
        List<Warehouse> list = List.of(
                new Warehouse(1L, "warehouse1", 1L),
                new Warehouse(2L, "warehouse2", 2L),
                new Warehouse(3L, "warehouse3", 3L)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Warehouse> page = new PageImpl<>(list, pageable, list.size());
        when(warehouseRepository.findAll(pageable)).thenReturn(page);

        //WHEN
        Page<Warehouse> result = warehouseService.findAll(pageable);

        //THEN
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void shouldThrowExceptionFindingCallWarehouses_whenPageableIsNull(){
        //WHEN
        Executable executable = () -> warehouseService.findAll(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Pageable can not be null", e.getMessage());

        verify(warehouseRepository,never()).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindOneWarehouse(){
        //GIVEN
        Warehouse warehouse = new Warehouse(1L, "warehouse", 1L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        //WHEN
        Warehouse result = warehouseService.findOne(1L);

        //THEN
        assertNotNull(result.getId());
        assertEquals("warehouse", result.getName());

        verify(warehouseRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionFindingById_whenNotFound(){
        //WHEN
        Executable executable = () -> warehouseService.findOne(1L);

        //THEN
        WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
        assertEquals("Warehouse not found", e.getMessage());

        verify(warehouseRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionFindingById_whenIdIsNull(){
        //WHEN
        Executable executable = () -> warehouseService.findOne(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Id can not be null", e.getMessage());

        verify(warehouseRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionFindingById_whenCurrentUserIsDifferentToWarehouseOwner(){
        //WHEN
        Executable executable = () -> warehouseService.findOne(2L);
        when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        //THEN
        WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
        assertEquals("Warehouse not found", e.getMessage());

        verify(warehouseRepository).findById(anyLong());
    }

    @Test
    void shouldCreateWarehouse(){
        //GIVEN
        WarehouseDTO dto = new WarehouseDTO(1L, "deposito");
        when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(new Warehouse(1L, "warehouse", 1L));

        //WHEN
        Warehouse result = warehouseService.create(dto);

        //THEN
        assertNotNull(result);
        assertEquals("warehouse", result.getName());
    }

    @Test
    void shouldThrowExceptionCreatingWarehouse_whenDTOIsNull(){
        //GIVEN


        //WHEN
        Executable executable = () -> warehouseService.create(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("You must provide a valid request body", e.getMessage());
    }
}