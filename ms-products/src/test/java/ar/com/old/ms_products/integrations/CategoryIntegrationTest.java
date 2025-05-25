package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
    private Warehouse warehouse;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        warehouse = new Warehouse(null, "warehouse", 1L);
        warehouseRepository.save(warehouse);

        Category category1 = new Category(null, "category1", warehouse);
        Category category2 = new Category(null, "category2", warehouse);
        Category category3 = new Category(null, "category3", warehouse);
        categoryRepository.saveAll(List.of(category1, category2, category3));
    }

    @AfterEach
    void clean() {
        categoryRepository.deleteAll();
        warehouseRepository.deleteAll();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("ALTER TABLE warehouses AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE categories AUTO_INCREMENT = 1");
    }

    @Test
    void shouldCreateCategory() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

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

    @Test
    void shouldFailCreatingCategory_whenAlreadyExist() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        categoryRepository.save(new Category(null, "technology", warehouse));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/categories")


                //THEN
                .then()
                .statusCode(409)
                .extract().response();

        assertThat(response.asString()).isEqualTo("{\"error\":\"Category already exist\"}");
    }

    @Test
    void shouldFindAllCategories(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/categories")

                //THEN
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> results = response.path("_embedded.categoryList");

        assertThat(results.size()).isEqualTo(3);
    }

    @Test
    void shouldFindOneCategory(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        Category response = given()
                .port(port)
                .contentType(ContentType.JSON)


                //WHEN
                .when()
                .get("/api/categories/1")

                //THEN
                .then()
                .statusCode(200)
                .extract().as(Category.class);

        assertThat(response.getName()).isEqualTo("category1");
    }

    @Test
    void shouldFailFindingOneCategory_whenNotFound(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/categories/10")

                //THEN
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response.asString()).isEqualTo("{\"error\":\"Category not found\"}");
    }

    @Test
    void shouldDeleteCategory(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .delete("/api/categories/1")

                //THEN
                .then()
                .statusCode(204);

        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void shouldFailDeletingOneCategory_whenNotFound(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .delete("/api/categories/10")

                //THEN
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response.asString()).isEqualTo("{\"error\":\"Category not found\"}");
    }
}
