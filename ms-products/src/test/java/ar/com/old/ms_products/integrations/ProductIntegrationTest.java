package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
public class ProductIntegrationTest {
    private final static String REQUEST_BODY = """
               {
                   "name":"Product 1",
                   "description": "Description 1",
                   "price": 100.00,
                   "categoryId": 1
               }
            """;

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;
    @MockitoBean
    private UserClientService userClientService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    private Category category;
    private Warehouse warehouse;

    @BeforeEach
    void init() {
        warehouse = new Warehouse(null, "warehouse", 1L);
        warehouseRepository.save(warehouse);
        category = new Category(null, "category", warehouse);
        categoryRepository.save(category);
    }


    @Test
    void shouldCreateProduct(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        ProductResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/products")


                //THEN
                .then()
                .statusCode(201)
                .extract().as(ProductResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Product 1");
        assertThat(response.price()).isEqualTo(100);
    }
}
