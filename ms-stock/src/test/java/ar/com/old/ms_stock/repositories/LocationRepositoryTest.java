package ar.com.old.ms_stock.repositories;

import ar.com.old.ms_stock.entities.Location;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Optional<Location> result = locationRepository.findByNameAndWarehouseIdAndActiveTrue("B1", 1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals("B1", result.get().getName());
    }

    @Test
    void shouldFindAllByWarehouseId(){
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);

        //WHEN
        Page<Location> result = locationRepository.findAllByWarehouseIdAndActiveTrue(pageable, 1L);

        //THEN
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.stream().anyMatch(location -> location.getName().equals("B1")));
        assertTrue(result.stream().anyMatch(location -> location.getName().equals("B2")));
    }

    @Test
    void shouldFindByIdAndWarehouseId(){
        //WHEN
        Optional<Location> result = locationRepository.findByIdAndWarehouseIdAndActiveTrue(1L, 1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals("B1", result.get().getName());
    }

    @Test
    @ExpectedDataSet(value = "location_delete.json")
    void shouldDeleteByIdAndWarehouseId(){
        //WHEN
        locationRepository.deleteByIdAndWarehouseId(1L, 1L);

        //THEN
        //View dataset locations_delete.json
    }
}