package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.entities.Product;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DBRider
@DataSet("products.json")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void shouldFindProductByNameAndWarehouseId(){
        //WHEN
        Optional<Product> result = repository.findByNameAndWarehouseId("product1", 1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals("product1", result.get().getName());
        assertEquals("description",result.get().getDescription());
    }

    @ParameterizedTest
    @CsvSource({
            "'invalid', 1",
            "'product1', 10",
            "'invalid', 10"
    })
    void shouldReturnOptionalEmpty_whenNotFound(String name, Long warehouseId){
        //WHEN
        Optional<Product> result = repository.findByNameAndWarehouseId(name,warehouseId);

        //THEN
        assertTrue(result.isEmpty());

    }

    @Test
    void shouldFindAllByWarehouseId(){
        //GIVEN
        Pageable pageable = PageRequest.of(0, 10);

        //WHEN
        Page<Product> result = repository.findAllByWarehouseId(pageable,1L);

        //THEN
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

}