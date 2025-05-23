package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.entities.Warehouse;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
public class WarehouseIntegrationTest {
    private static final String REQUEST_BODY = """
            {
                "id": 1,
                "name": "Warehouse"
            }
            """;

    @LocalServerPort
    private int port;
    @MockitoBean
    private UserClientService userClientService;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private DataSource dataSource;


    @BeforeEach
    void init() {
        Warehouse warehouse1 = new Warehouse(null, "warehouse1", 1L);
        Warehouse warehouse2 = new Warehouse(null, "warehouse2", 2L);
        Warehouse warehouse3 = new Warehouse(null, "warehouse3", 3L);
        warehouseRepository.saveAll(List.of(warehouse1, warehouse2, warehouse3));
    }

    @AfterEach
    void clean() {
        warehouseRepository.deleteAll();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("ALTER TABLE warehouses AUTO_INCREMENT = 1");

    }

    @Test
    void shouldCreateWarehouse() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(4L, "user", "user@mail.com"));
        Warehouse response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/warehouses")

                //THEN
                .then()
                .statusCode(201)
                .log().all()
                .extract().as(Warehouse.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Warehouse");
    }

    @Test
    void shouldFailCreatingWarehouse_whenUserAlreadyHasWarehouse() {
        //GIVEN
        warehouseRepository.save(new Warehouse(null, "warehouseMock", 4L));
        when(userClientService.getUser()).thenReturn(new UserDTO(4L, "user", "user@mail.com"));
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .post("/api/warehouses")

                //THEN
                .then()
                .statusCode(409)
                .extract()
                .response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"You already have a registered warehouse\"}");
    }

    @Test
    void shouldFindAllWarehouses() {
        //GIVEN
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/warehouses")

                //THEN
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> responseList = response.path("_embedded.warehouseList");
        assertThat(responseList).isNotNull();
        assertThat(responseList.size()).isEqualTo(3);
    }

    @Test
    void shouldFindOneWarehouse() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Warehouse response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/warehouses/1")

                //THEN
                .then()
                .statusCode(200)
                .extract().as(Warehouse.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("warehouse1");
    }

    @Test
    void shouldFailFindingOneWarehouse_whenNotFound() {
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/warehouses/15")

                //THEN
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"Warehouse not found\"}");
    }


    @Test
    void shouldUpdateWarehouse() {
        //GIVEN
        String body = """
                 {
                    "id": 1,
                    "name": "new name"
                }
                """;
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
        Warehouse response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(body)

                //WHEN
                .when()
                .put("/api/warehouses")

                //THEN
                .then()
                .statusCode(200)
                .log().all()
                .extract().as(Warehouse.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("new name");
    }

}
