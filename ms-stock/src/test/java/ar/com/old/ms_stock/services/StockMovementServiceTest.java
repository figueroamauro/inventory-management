package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.exceptions.ProductNotFoundException;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {
    @InjectMocks
    private StockMovementServiceImpl stockMovementService;
    @Mock
    private StockEntryRepository stockEntryRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private ProductsClientService productsClientService;
    @Mock
    private LocationRepository locationRepository;

    private WarehouseDTO warehouseDTO;
    private StockEntry stockEntry;
    private StockMovementDTO dto;
    private StockMovement stockMovement;
    private Location location;
    private ProductDTO productDTO;

    @BeforeEach
    void init() {
        warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
        stockEntry = new StockEntry(20, 1L, 1L);
        dto = new StockMovementDTO(MovementType.IN, 20, "", 1L, 1L);
        location = new Location(1L, "B1", 1L);
        productDTO = new ProductDTO(1L, "product", "description", 100.00, 1L, LocalDateTime.now());
        stockMovement = new StockMovement(1L, MovementType.IN, 20, "", location, stockEntry);
    }

    @Nested
    class Create {

        @Test
        void shouldCreateStockEntryBeforeToCreateFirstMovement() {
            //GIVEN
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(new Location(1L, "B1", 1L)));
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);

            //WHEN
            stockMovementService.create(dto);

            //THEN
            verify(stockEntryRepository).findByIdAndWarehouseId(1L, 1L);
            verify(stockEntryRepository).save(any(StockEntry.class));
        }

        @Test
        void shouldCreateStockMovement() {
            //GIVEN
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(location));
            when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(stockMovement);
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);

            //WHEN
            StockMovement result = stockMovementService.create(dto);

            //THEN
            assertNotNull(result);
            assertEquals(20, result.getQuantity());
            assertEquals(1L, result.getStockEntry().getId());

            verify(stockEntryRepository).findByIdAndWarehouseId(1L, 1L);
            verify(locationRepository).findByIdAndWarehouseId(1L, 1L);
            verify(stockEntryRepository).save(any(StockEntry.class));
        }

        @Test
        void shouldFailCreatingMovement_whenLocationNotExists(){
            //GIVEN
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);

            //WHEN
            Executable executable = () -> stockMovementService.create(dto);

            //THEN
            LocationNotFoundException e = assertThrows(LocationNotFoundException.class, executable);
            assertEquals("Location not found", e.getMessage());

            verify(locationRepository).findByIdAndWarehouseId(1L, 1L);
            verify(stockEntryRepository,never()).save(any(StockEntry.class));
        }

        @Test
        void shouldFailCreatingMovement_whenProductNotExists(){
            //WHEN
            Executable executable = () -> stockMovementService.create(dto);

            //THEN
            ProductNotFoundException e = assertThrows(ProductNotFoundException.class, executable);
            assertEquals("Product not found", e.getMessage());

            verify(stockEntryRepository,never()).save(any(StockEntry.class));
        }

        @Test
        void shouldFailCreatingMovement_whenDTOIsNull(){
            //WHEN
            Executable executable = () -> stockMovementService.create(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("DTO can not be null", e.getMessage());

            verify(stockEntryRepository,never()).save(any(StockEntry.class));
        }
    }
}
