package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static io.restassured.RestAssured.*;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
public class CategoryIntegrationTest {
    private static final String REQUEST_BODY = """
            {
            "name": "technology"
            }
            """;

    @LocalServerPort
    private int port;
    @Autowired
    private CategoryRepository categoryRepository;
    @MockitoBean
    private UserClientService userClientService;
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void shouldCreateCategory() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Warehouse warehouse = new Warehouse(null, "warehouse", 1L);
        warehouseRepository.save(warehouse);

        Category result = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/categories")

                //THEN
                .then()
                .statusCode(201)
                .extract().as(Category.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("technology");
    }
}
