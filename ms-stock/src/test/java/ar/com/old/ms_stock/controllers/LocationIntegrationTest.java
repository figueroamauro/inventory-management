package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.TestcontainersConfiguration;
import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("integration")
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class LocationIntegrationTest {
    @LocalServerPort
    private int port;
    @MockitoBean
    private ProductsClientService productsClientService;

    @Test
    void shouldCreateLocation() {
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        given()
                .baseUri("http://localhost")
                .port(port)

                .contentType(ContentType.JSON)
                .body("{\"name\":\"B1\"}")
                .log().all()

        //WHEN
                .when()
                .post("/api/locations")

        //THEN
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("name", equalTo("B1"))
                .body("products", equalTo(Collections.emptyList()));
    }
}
