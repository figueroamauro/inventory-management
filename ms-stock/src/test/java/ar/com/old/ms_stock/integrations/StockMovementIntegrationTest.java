package ar.com.old.ms_stock.integrations;

import ar.com.old.ms_stock.TestcontainersConfiguration;
import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.ProductDTO;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.StockMovementResponseDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.repositories.LocationRepository;
import ar.com.old.ms_stock.repositories.StockMovementRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
@ExtendWith(SpringExtension.class)
public class StockMovementIntegrationTest {
    @LocalServerPort
    private int port;
    @MockitoBean
    private ProductsClientService productsClientService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private StockMovementRepository stockMovementRepository;
    private ProductDTO productDTO;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void init() {
        productDTO = new ProductDTO(1L, "product", "description", 100.00, 1L, LocalDateTime.now());
        warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
        Location location = new Location(null, "B1", 1L);
        locationRepository.save(location);
    }

    @AfterEach
    void clean() {
        stockMovementRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    void shouldCreateMovement() {
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
        Mockito.when(productsClientService.getProduct(1L)).thenReturn(productDTO);

        String requestBody = """
                {
                "type": "IN",
                "quantity": 100,
                "note": "",
                "locationId": 1,
                "productId": 1
                }
                """;

        StockMovementResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)

                //WHEN
                .when()
                .post("/api/movements")

                //THEN
                .then()
                .statusCode(201)
                .extract().as(StockMovementResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.quantity()).isEqualTo(100);
        assertThat(response.stock()).isEqualTo(100);
        assertThat(response.beforeStock()).isEqualTo(0);

    }
}
