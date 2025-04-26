package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.WarehouseAlreadyExistException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
    private Warehouse warehouse;
    private WarehouseDTO dto;


    @BeforeEach
    void init() {
        warehouse = new Warehouse(1L, "warehouse", 1L);
        dto = new WarehouseDTO(1L, "deposito");
    }

    @Nested
    class FindAll {

        @Test
        void shouldFindAllWarehouses() {
            //GIVEN
            List<Warehouse> list = List.of(warehouse, warehouse, warehouse);
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
        void shouldThrowExceptionFindingCallWarehouses_whenPageableIsNull() {
            //WHEN
            Executable executable = () -> warehouseService.findAll(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Pageable can not be null", e.getMessage());

            verify(warehouseRepository, never()).findAll(any(Pageable.class));
        }
    }

    @Nested
    class findOne {

        @Test
        void shouldFindOneWarehouse() {
            //GIVEN
            when(warehouseRepository.findByIdAndUserId(1L,1L)).thenReturn(Optional.of(warehouse));
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

            //WHEN
            Warehouse result = warehouseService.findOne(1L);

            //THEN
            assertNotNull(result.getId());
            assertEquals("warehouse", result.getName());

            verify(warehouseRepository).findByIdAndUserId(1L,1L);
        }

        @Test
        void shouldThrowExceptionFindingById_whenNotFound() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

            //WHEN
            Executable executable = () -> warehouseService.findOne(1L);

            //THEN
            WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
            assertEquals("Warehouse not found", e.getMessage());

            verify(warehouseRepository).findByIdAndUserId(1L,1L);
        }

        @Test
        void shouldThrowExceptionFindingById_whenIdIsNull() {
            //WHEN
            Executable executable = () -> warehouseService.findOne(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(warehouseRepository, never()).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionFindingById_whenCurrentUserIsDifferentToWarehouseOwner() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

            //WHEN
            Executable executable = () -> warehouseService.findOne(2L);

            //THEN
            WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
            assertEquals("Warehouse not found", e.getMessage());

            verify(warehouseRepository).findByIdAndUserId(anyLong(),anyLong());
        }
    }

    @Nested
    class Create {

        @Test
        void shouldCreateWarehouse() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(new Warehouse(1L, "warehouse", 1L));

            //WHEN
            Warehouse result = warehouseService.create(dto);

            //THEN
            assertNotNull(result);
            assertEquals("warehouse", result.getName());

            verify(warehouseRepository).save(any(Warehouse.class));
        }

        @Test
        void shouldThrowExceptionCreatingWarehouse_whenDTOIsNull() {
            //WHEN
            Executable executable = () -> warehouseService.create(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("You must provide a valid request body", e.getMessage());

            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }

        @Test
        void shouldThrowExceptionCreatingWarehouse_whenAlreadyExist() {
            //GIVEN
            when(warehouseRepository.findByNameAndUserId("deposito", 1L)).thenReturn(Optional.of(new Warehouse(1L, "warehouse", 1L)));
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

            //WHEN
            Executable executable = () -> warehouseService.create(dto);

            //THEN
            WarehouseAlreadyExistException e = assertThrows(WarehouseAlreadyExistException.class, executable);
            assertEquals("Warehouse already exist", e.getMessage());

            verify(warehouseRepository).findByNameAndUserId(anyString(), anyLong());
            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateWarehouse(){
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
            when(warehouseRepository.findByNameAndUserId("deposito", 1L)).thenReturn(Optional.of(new Warehouse(1L, "warehouse", 1L)));
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(new Warehouse(1L, "warehouse", 1L));

            //WHEN
            Warehouse result = warehouseService.update(dto);

            //THEN
            assertNotNull(result);
            assertEquals("warehouse", result.getName());

            verify(warehouseRepository).save(any(Warehouse.class));
        }

        @Test
        void shouldThrowExceptionUpdatingWarehouse_whenDTOIsNull() {
            //WHEN
            Executable executable = () -> warehouseService.update(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("You must provide a valid request body", e.getMessage());

            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }

        @Test
        void shouldThrowExceptionUpdatingWarehouse_whenIdIsNull() {
            //GIVEN
            dto = new WarehouseDTO(null, "deposito");

            //WHEN
            Executable executable = () -> warehouseService.update(dto);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }

        @Test
        void shouldThrowExceptionUpdatingWarehouse_whenNotFoundWarehouse() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
            when(warehouseRepository.findByNameAndUserId("deposito", 1L)).thenReturn(Optional.empty());

            //WHEN
            Executable executable = () -> warehouseService.update(dto);

            //THEN
            WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
            assertEquals("Warehouse not found", e.getMessage());

            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteWarehouse(){
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
            when(warehouseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.ofNullable(warehouse));

            //WHEN
            warehouseService.delete(1L);

            //THEN
            verify(warehouseRepository).deleteById(1L);
        }

        @Test
        void shouldThrowExceptionDeletingWarehouse_whenIdIsNull() {
            //WHEN
            Executable executable = () -> warehouseService.delete(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(warehouseRepository, never()).save(any(Warehouse.class));
        }
    }
}
