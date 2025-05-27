package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductResponseDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

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
    @Autowired
    private DataSource dataSource;
    private Category category;
    private Warehouse warehouse;
    private Product product;

    @BeforeEach
    void init() {
        warehouse = new Warehouse(null, "warehouse", 1L);
        warehouseRepository.save(warehouse);
        category = new Category(null, "category", warehouse);
        categoryRepository.save(category);
        product = new Product(null, "Product 1", "", 100.00, category, warehouse);
    }

    @AfterEach
    void clean() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        warehouseRepository.deleteAll();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("ALTER TABLE products AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE categories AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE warehouses AUTO_INCREMENT = 1");
    }

    @Test
    void shouldCreateProduct() {
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

    @Test
    void shouldFailCreatingProduct_whenNameAlreadyExist() {
        //GIVEN
        productRepository.save(product);
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/products")

                //THEN
                .then()
                .statusCode(409)
                .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"Product already exist\"}");
    }

    @Test
    void shouldFindAllProducts() {
        //GIVEN
        Product product1 = new Product(null, "Product 1", "", 100.00, category, warehouse);
        Product product2 = new Product(null, "Product 2", "", 100.00, category, warehouse);
        productRepository.saveAll(List.of(product1, product2));

        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/products")

                //THEN
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> results = response.path("_embedded.productResponseDTOList");

        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    void shouldFindOneProduct() {
        //GIVEN
        productRepository.save(product);
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        ProductResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/products/1")

                //THEN
                .then()
                .statusCode(200)
                .extract().as(ProductResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Product 1");
    }

    @Test
    void shouldFailFindingOneProduct_whenNotFound() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/products/1")

                //THEN
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"Product not found\"}");
    }

    @Test
    void shouldUpdateProduct() {
        //GIVEN
        productRepository.save(product);
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        ProductResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "id": 1,
                        "name":"new name",
                        "description": "new description",
                        "price": 50.00,
                        "categoryId": 1
                       }
                    """
                )

                //WHEN
                .when()
                .put("/api/products")

                //THEN
                .then()
                .statusCode(200)
                .extract().as(ProductResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("new name");
        assertThat(response.price()).isEqualTo(50);
        assertThat(response.description()).isEqualTo("new description");
    }
}
