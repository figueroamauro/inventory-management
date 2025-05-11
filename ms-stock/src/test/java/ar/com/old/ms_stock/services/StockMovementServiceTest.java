package ar.com.old.ms_stock.services;

import ar.com.old.ms_stock.clients.WarehouseClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.repositories.StockEntryRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
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
class StockMovementServiceTest {

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;
    @Mock
    private StockEntryRepository stockEntryRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private WarehouseClientService clientService;

    @Test
    void shouldCreateStockEntryBeforeToCreateFirstMovement(){
        //GIVEN
        WarehouseDTO warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
        StockEntry stockEntry = new StockEntry(20, 1L, 1L);
        StockMovementDTO dto = new StockMovementDTO(MovementType.IN, 20, "", 1L, 1L);
        when(clientService.getWarehouse()).thenReturn(warehouseDTO);
        when(stockEntryRepository.save(any(StockEntry.class))).thenReturn(stockEntry);

        //WHEN
        stockMovementService.create(dto);

        //THEN
        verify(stockEntryRepository).findByIdAndWarehouseId(1L,1L);
        verify(stockEntryRepository).save(any(StockEntry.class));
    }
}