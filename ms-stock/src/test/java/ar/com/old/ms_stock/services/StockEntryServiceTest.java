package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StockEntryServiceTest {

    @InjectMocks
    private StockEntryServiceImpl stockEntryService;
    @Mock
    private StockEntryRepository stockEntryRepository;
    @Mock
    private ProductsClientService productsClientService;
    private StockEntry stockEntry;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void init() {
        stockEntry = new StockEntry(100, 1L, 1L);
        warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
    }

    @Test
    void shouldFindAllStockEntry(){
        //GIVEN
        List<StockEntry> list = List.of(stockEntry, stockEntry, stockEntry);
        Pageable pageable = PageRequest.of(0, 10);
        Page<StockEntry> page = new PageImpl<>(list, pageable, list.size());
        when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
        when(stockEntryRepository.findAllByWarehouseId(pageable,1L)).thenReturn(page);

        //WHEN
        Page<StockEntry> result = stockEntryService.findAll(pageable);

        //THEN
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());

        verify(productsClientService).getWarehouse();
        verify(stockEntryRepository).findAllByWarehouseId(any(Pageable.class), anyLong());
    }
    
    @Test
    void shouldFindOneStockEntry(){
        //GIVEN
        when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
        when(stockEntryRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.ofNullable(stockEntry));
    
        //WHEN
        StockEntry result = stockEntryService.findOne(1L);

        //THEN
        assertNotNull(result);
        assertEquals(100, result.getQuantity());

    }
}