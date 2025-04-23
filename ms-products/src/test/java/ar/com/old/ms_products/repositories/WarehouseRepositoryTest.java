package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.entities.Warehouse;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DataSet("warehouse.json")
class WarehouseRepositoryTest {

    @Autowired
    private WarehouseRepository repository;

    @Test
    void shouldFindWarehouseByUserId(){
        //WHEN
        Optional<Warehouse> result = repository.findByUserId(1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("deposito", result.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalFindingWarehouseByUserId_whenNotFound(){
        //WHEN
        Optional<Warehouse> result = repository.findByUserId(10L);

        //THEN
        assertTrue(result.isEmpty());
    }
}