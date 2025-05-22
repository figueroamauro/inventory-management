package ar.com.old.ms_products.integrations;

import ar.com.old.ms_products.TestcontainersConfiguration;
import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.entities.Warehouse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
public class WarehouseIntegrationTest {
    private static final String REQUEST_BODY = """
            {
                "name": "Warehouse"
            }
            """;

    @LocalServerPort
    private int port;
    @MockitoBean
    private UserClientService userClientService;


    @Test
    void shouldCreateWarehouse(){
        //GIVEN
        when(userClientService.getUser()).thenReturn(new UserDTO(1L, "user", "user@mail.com"));
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
}
