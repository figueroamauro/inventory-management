package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.LocationStock;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.exceptions.LocationConflictException;
import ar.com.old.ms_stock.exceptions.NegativeStockException;
import ar.com.old.ms_stock.exceptions.ProductConflictException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
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
    private Pageable pageable;
    private Page<StockMovement> page;

    @BeforeEach
    void init() {
        warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
        stockEntry = new StockEntry(20, 1L, 1L);
        dto = new StockMovementDTO("IN", 20, "", 1L, 1L);
        location = new Location(1L, "B1", 1L);
        productDTO = new ProductDTO(1L, "productName", "description", 100.00, 1L, LocalDateTime.now());
        stockMovement = new StockMovement(1L, MovementType.IN, 20,stockEntry.getQuantity(),stockEntry.getQuantity(), "", location, stockEntry);
        List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
        pageable = PageRequest.of(0, 10);
        page = new PageImpl<>(list, pageable, list.size());
    }

    @Nested
    class Create {

        @Test
        void shouldCreateStockEntryBeforeToCreateFirstMovement() {
            //GIVEN
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
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
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
            when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(stockMovement);
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);

            //WHEN
            StockMovement result = stockMovementService.create(dto);

            //THEN
            assertNotNull(result);
            assertEquals(20, result.getQuantity());
            assertEquals(1L, result.getStockEntry().getId());

            verify(stockEntryRepository).findByIdAndWarehouseId(1L, 1L);
            verify(locationRepository).findByIdAndWarehouseIdAndActiveTrue(1L, 1L);
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
            LocationConflictException e = assertThrows(LocationConflictException.class, executable);
            assertEquals("Location not found", e.getMessage());

            verify(locationRepository).findByIdAndWarehouseIdAndActiveTrue(1L, 1L);
            verify(stockEntryRepository,never()).save(any(StockEntry.class));
        }

        @Test
        void shouldFailCreatingMovement_whenProductNotExists(){
            //WHEN
            Executable executable = () -> stockMovementService.create(dto);

            //THEN
            ProductConflictException e = assertThrows(ProductConflictException.class, executable);
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

        @Test
        void shouldModifyStockEntryCreatingMovement_whenTypeIsIN(){
            //GIVEN
            stockEntry = new StockEntry(0, 1L, 1L);
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);
            dto = new StockMovementDTO("IN", 40, "", 1L, 1L);

            //WHEN
            stockMovementService.create(dto);

            //THEN
            assertEquals(40, stockEntry.getQuantity());
        }

        @Test
        void shouldModifyStockEntryCreatingMovement_whenTypeIsOUT(){
            //GIVEN
            location = new Location(1L, "B1", 1L);
            location.getLocationStockList().add(new LocationStock(1L,1L,100,location));
            stockEntry = new StockEntry(100, 1L, 1L);
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);
            dto = new StockMovementDTO("OUT", 40, "", 1L, 1L);

            //WHEN
            stockMovementService.create(dto);

            //THEN
            assertEquals(60, stockEntry.getQuantity());
        }

        @Test
        void shouldModifyStockEntryCreatingMovement_whenTypeIsRETURN(){
            //GIVEN
            location = new Location(1L, "B1", 1L);
            location.getLocationStockList().add(new LocationStock(1L,1L,100,location));
            stockEntry = new StockEntry(100, 1L, 1L);
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);
            dto = new StockMovementDTO("RETURN", 40, "", 1L, 1L);

            //WHEN
            stockMovementService.create(dto);

            //THEN
            assertEquals(60, stockEntry.getQuantity());
        }

        @Test
        void shouldFailCreatingMovement_whenStockGoesNegative(){
            //GIVEN
            location = new Location(1L, "B1", 1L);
            location.getLocationStockList().add(new LocationStock(1L,1L,100,location));
            stockEntry = new StockEntry(0, 1L, 1L);
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);
            when(locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(location));
            when(productsClientService.getProduct(1L)).thenReturn(productDTO);
            dto = new StockMovementDTO("RETURN", 40, "", 1L, 1L);

            //WHEN
            Executable executable = () -> stockMovementService.create(dto);

            //THEN
            NegativeStockException e = assertThrows(NegativeStockException.class, executable);
            assertEquals("Stock can not be negative for productName ID: " + dto.productId(),e.getMessage());
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldFindAllMovementsByWarehouseId(){
            //GIVEN
            when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
            when(stockMovementRepository.findAllByStockEntry_WarehouseId(pageable, 1L)).thenReturn(page);

            //WHEN
            Page<StockMovement> result = stockMovementService.findAll(pageable);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());

            verify(stockMovementRepository).findAllByStockEntry_WarehouseId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFindAllMovementsByProductId(){
            //GIVEN
            List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
            Pageable pageable = PageRequest.of(0, 10);
            Page<StockMovement> page = new PageImpl<>(list, pageable, list.size());
            when(stockMovementRepository.findAllByStockEntry_ProductId(pageable, 1L)).thenReturn(page);


            //WHEN
            Page<StockMovement> result = stockMovementService.findAllByProductId(pageable,1L);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());

            verify(stockMovementRepository).findAllByStockEntry_ProductId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFailFindingMovementByProductId_whenIdIsNull(){
            //WHEN
            Executable executable = () -> stockMovementService.findAllByProductId(Pageable.unpaged(), null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(stockMovementRepository,never()).findAllByStockEntry_ProductId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFindAllMovementsByLocationId(){
            //GIVEN
            when(stockMovementRepository.findAllByLocationId(pageable, 1L)).thenReturn(page);

            //WHEN
            Page<StockMovement> result = stockMovementService.findAllByLocationId(pageable,1L);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());

            verify(stockMovementRepository).findAllByLocationId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFailFindingMovementByLocationId_whenIdIsNull(){
            //WHEN
            Executable executable = () -> stockMovementService.findAllByLocationId(Pageable.unpaged(), null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(stockMovementRepository,never()).findAllByLocationId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFindAllMovementsByLocationIdAndProductId(){
            //GIVEN
            when(stockMovementRepository.findAllByLocationIdAndStockEntry_ProductId(pageable, 1L, 1L)).thenReturn(page);

            //WHEN
            Page<StockMovement> result = stockMovementService.findAllByLocationIdAndProductId(pageable,1L,1L);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());

            verify(stockMovementRepository).findAllByLocationIdAndStockEntry_ProductId(any(Pageable.class), anyLong(),anyLong());
        }

        @Test
        void shouldFailFindingMovementByLocationIdAndProductId_whenProductIdIsNull(){
            //WHEN
            Executable executable = () -> stockMovementService.findAllByLocationIdAndProductId(Pageable.unpaged(),1L, null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Product id can not be null", e.getMessage());

            verify(stockMovementRepository,never()).findAllByLocationId(any(Pageable.class), anyLong());
        }

        @Test
        void shouldFailFindingMovementByLocationIdAndProductId_whenLocationIdIsNull(){
            //WHEN
            Executable executable = () -> stockMovementService.findAllByLocationIdAndProductId(Pageable.unpaged(),null, 1L);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Location id can not be null", e.getMessage());

            verify(stockMovementRepository,never()).findAllByLocationId(any(Pageable.class), anyLong());
        }
    }
}
