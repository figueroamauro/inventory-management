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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
    void shouldFindAllWarehouses() {
        //GIVEN

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(REQUEST_BODY)

                //WHEN
                .when()
                .get("/api/warehouses")

                //THEN
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();

        List<Map<String,Object>> responseList = response.path("_embedded.warehouseList");
        assertThat(responseList).isNotNull();
        assertThat(responseList.size()).isEqualTo(3);

    }
}
