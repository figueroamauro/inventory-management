package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.StockMovement;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DBRider
@DataSet(value = "movements.json")
class StockMovementRepositoryTest {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Test
    void shouldVerifyIfExistByLocationId(){
        //WHEN
        boolean trueResult = stockMovementRepository.existsByLocationId(1L);
        boolean falseResult = stockMovementRepository.existsByLocationId(5L);

        //THEN
        assertTrue(trueResult);
        assertFalse(falseResult);
    }

    @Test
    void shouldFindAllByWarehouseId(){
        //WHEN
        Page<StockMovement> result = stockMovementRepository.findAllByStockEntry_WarehouseId(Pageable.unpaged(), 1L);

        //THEN
        assertNotNull(result);
        assertEquals(4,result.getTotalElements());
    }

    @Test
    void shouldFindAllByProductId(){
        //WHEN
        Page<StockMovement> result = stockMovementRepository.findAllByStockEntry_ProductId(Pageable.unpaged(), 2L);

        //THEN
        assertNotNull(result);
        assertEquals(2,result.getTotalElements());
    }

    @Test
    void shouldFindAllByLocationId(){
        //WHEN
        Page<StockMovement> result = stockMovementRepository.findAllByLocationId(Pageable.unpaged(), 3L);

        //THEN
        assertNotNull(result);
        assertEquals(2,result.getTotalElements());
    }
}