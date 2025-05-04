package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.repositories.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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

    @Test
    void shouldCreateLocation() {
        //GIVEN
        Location location = new Location(1L, "B2", 1L);
        LocationDTO dto = new LocationDTO(1L, "B2");
        when(clientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse1", 1L));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        //WHEN
        Location result = locationService.create(dto);

        //THEN
        assertNotNull(result);
        assertEquals("B2", result.getName());

        verify(locationRepository).save(any(Location.class));
    }

}