package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @InjectMocks
    private LocationServiceImpl locationService;
    @Mock
    private WarehouseClientService clientService;
    @Mock
    private LocationRepository locationRepository;

    private Location location;
    private LocationDTO dto;

    @BeforeEach
    void init() {
        location = new Location(1L, "B2", 1L);
        dto = new LocationDTO(1L, "B2");
    }

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

        verify(locationRepository,never()).save(any(Location.class));
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

        verify(locationRepository,never()).save(any(Location.class));
    }



}