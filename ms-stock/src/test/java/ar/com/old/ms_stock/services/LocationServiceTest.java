package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.exceptions.LocationInUseException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @InjectMocks
    private LocationServiceImpl locationService;
    @Mock
    private ProductsClientService clientService;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;

    private Location location;
    private LocationDTO dto;

    @BeforeEach
    void init() {
        location = new Location(1L, "B2", 1L);
        dto = new LocationDTO(1L, "B2");
    }

    @Nested
    class Create {

        @Test
        void shouldCreateLocation() {
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.save(any(Location.class))).thenReturn(location);

            //WHEN
            Location result = locationService.create(dto);

            //THEN
            assertNotNull(result);
            assertEquals("B2", result.getName());

            verify(locationRepository).save(any(Location.class));
        }

        @Test
        void shouldFailCreatingLocation_whenDTOIsNull() {
            //WHEN
            Executable executable = () -> locationService.create(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("DTO can not be null", e.getMessage());

            verify(locationRepository, never()).save(any(Location.class));
        }

        @Test
        void shouldFailCreatingLocation_whenNameAlreadyExist() {
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.findByNameAndWarehouseId("B2", 1L)).thenReturn(Optional.ofNullable(location));

            //WHEN
            Executable executable = () -> locationService.create(dto);

            //THEN
            LocationAlreadyExistException e = assertThrows(LocationAlreadyExistException.class, executable);
            assertEquals("Location already exist", e.getMessage());

            verify(locationRepository, never()).save(any(Location.class));
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldFindAllLocations(){
            //GIVEN
            List<Location> list = List.of(location, location, location);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Location> page = new PageImpl<>(list, pageable, list.size());
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.findAllByWarehouseId(pageable,1L)).thenReturn(page);

            //WHEN
            Page<Location> result = locationService.findAll(pageable);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());

            verify(locationRepository).findAllByWarehouseId(pageable,1L);
        }
    }

    @Nested
    class FindOne {

        @Test
        void shouldFindOne(){
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.ofNullable(location));

            //WHEN
            Location result = locationService.findOne(1L);

            //THEN
            assertNotNull(result);
            assertEquals("B2", result.getName());

            verify(locationRepository).findByIdAndWarehouseId(1L, 1L);
        }
    }

    @Test
    void shouldFailFindingById_whenNotFound(){
        //GIVEN
        when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));

        //WHEN
        Executable executable = () -> locationService.findOne(1L);

        //THEN
        LocationNotFoundException e = assertThrows(LocationNotFoundException.class, executable);
        assertEquals("Location not found", e.getMessage());

        verify(locationRepository).findByIdAndWarehouseId(anyLong(),anyLong());
    }

    @Test
    void shouldFailFindingById_whenIdIsNull(){
        //WHEN
        Executable executable = () -> locationService.findOne(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Id can not be null", e.getMessage());

        verify(locationRepository, never()).findByIdAndWarehouseId(anyLong(), anyLong());
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateLocation(){
            //GIVEN
            dto = new LocationDTO(1L, "new name");
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.ofNullable(location));
            when(locationRepository.save(any(Location.class))).thenReturn(location);

            //WHEN
            Location result = locationService.update(dto);

            //THEN
            assertNotNull(result);
            assertEquals("new name", result.getName());

            verify(locationRepository).save(location);
        }

        @Test
        void shouldFailUpdatingLocation_whenNotFound(){
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));

            //WHEN
            Executable executable = () -> locationService.update(dto);

            //THEN
            LocationNotFoundException e = assertThrows(LocationNotFoundException.class, executable);
            assertEquals("Location not found", e.getMessage());

            verify(locationRepository,never()).save(any(Location.class));
        }

        @Test
        void shouldFailUpdatingLocation_whenDTOIsNull(){
            //WHEN
            Executable executable = () -> locationService.update(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("DTO can not be null", e.getMessage());

            verify(locationRepository,never()).save(any(Location.class));
        }

        @Test
        void shouldFailUpdatingLocation_whenIdIsNull(){
            //GIVEN
            dto = new LocationDTO(null, "B2");

            //WHEN
            Executable executable = () -> locationService.update(dto);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(locationRepository,never()).save(any(Location.class));
        }

        @Test
        void shouldFailUpdatingLocation_whenAlreadyExist(){
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(locationRepository.findByNameAndWarehouseId("B2", 1L)).thenReturn(Optional.ofNullable(location));

            //WHEN
            Executable executable = () -> locationService.update(dto);

            //THEN
            LocationAlreadyExistException e = assertThrows(LocationAlreadyExistException.class, executable);
            assertEquals("Location already exist", e.getMessage());

            verify(locationRepository,never()).save(any(Location.class));
        }
    }

    @Nested
    class Delete{

        @Test
        void shouldDeleteById(){
            //GIVEN
            when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
            when(stockMovementRepository.existsByLocationId(1L)).thenReturn(false);

            //WHEN
            locationService.delete(1L);

            //THEN
            verify(locationRepository).deleteByIdAndWarehouseId(1L,1L);
        }

        @Test
        void shouldFailDeletingById_whenIdIsNull(){
            //WHEN
            Executable executable = () -> locationService.delete(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(locationRepository, never()).deleteById(anyLong());
        }

        @Test
        void shouldFailDeletingById_whenHasStock(){
            //GIVEN
            when(stockMovementRepository.existsByLocationId(1L)).thenReturn(true);

            //WHEN
            Executable executable = () -> locationService.delete(1L);

            //THEN
            LocationInUseException e = assertThrows(LocationInUseException.class, executable);
            assertEquals("Location is in use and cannot be deleted", e.getMessage());

            verify(locationRepository, never()).deleteByIdAndWarehouseId(anyLong(),anyLong());
        }
    }
}
