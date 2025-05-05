package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.Location;
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


@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DataSet(value = "locations.json")
class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

    @Test
    void shouldFindByNameAndWarehouseId() {
        //WHEN
        Optional<Location> result = locationRepository.findByNameAndWarehouseId("B1", 1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals("B1", result.get().getName());
    }
}