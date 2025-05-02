package ar.com.old.ms_products.repositories;

import ar.com.old.ms_products.MsProductsApplication;
import ar.com.old.ms_products.clients.UserClient;
import ar.com.old.ms_products.config.FeignConfig;
import ar.com.old.ms_products.entities.Category;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, schema = "PUBLIC")
@DataSet("category.json")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    void shouldFindCategoryByName(){
        //WHEN
        Optional<Category> result = repository.findByNameAndWarehouseId("Electro",1L);

        //THEN
        //View dataset category.json
        assertTrue(result.isPresent());
        assertEquals("Electro", result.get().getName());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getWarehouse().getId());
    }
    @Test
    void shouldReturnEmptyOptional_whenNameIsNotFound(){
        //WHEN
        Optional<Category> result = repository.findByNameAndWarehouseId("test",1L);

        //THEN
        //View dataset category.json
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindCategoryByIdAndWarehouseId(){
        //WHEN
        Optional<Category> result = repository.findByIdAndWarehouseId(1L, 1L);

        //THEN
        assertTrue(result.isPresent());
        assertEquals("Electro", result.get().getName());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getWarehouse().getId());
    }
}