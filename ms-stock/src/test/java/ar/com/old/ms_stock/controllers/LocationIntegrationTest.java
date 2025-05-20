package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.TestcontainersConfiguration;
import ar.com.old.ms_stock.clients.ProductsClientService;
import ar.com.old.ms_stock.clients.dto.WarehouseDTO;
import ar.com.old.ms_stock.dto.LocationResponseDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.repositories.LocationRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("integration")
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class LocationIntegrationTest {
    @LocalServerPort
    private int port;
    @MockitoBean
    private ProductsClientService productsClientService;
    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void cleanup() {
        locationRepository.deleteAll();
    }

    @Test
    void shouldCreateLocation() {
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        LocationResponseDTO response = given()
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
                .extract().as(LocationResponseDTO.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.id()).isGreaterThan(0);
        assertThat(response.name()).isEqualTo("B1");
    }

    @Test
    void shouldFindAllLocations_whenLocationsExist(){
        //GIVEN
        Location location1 = new Location(null, "location 1", 1L);
        Location location2 = new Location(null, "location 2", 1L);
        locationRepository.saveAll(List.of(location1, location2));
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/locations")

                //THEN
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> content = response.jsonPath().getList("_embedded.locationResponseDTOList");

        assertThat(content).isNotEmpty();
        assertThat(content.get(0)).containsKey("id");
        assertThat(content.size()).isEqualTo(2);
    }

    @Test
    void shouldFailFindingAllLocations_whenNotExist(){
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/locations")

                //THEN
                .then()
                .statusCode(200)
                .extract().response();
    }

    @Test
    void shouldFindOneLocation(){
        //GIVEN
        Location location = new Location(null, "location 1", 1L);
        Location savedLocation = locationRepository.save(location);
        Long id = savedLocation.getId();
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        LocationResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/locations/"+id)

                //THEN
                .then()
                .statusCode(200)
                .extract().as(LocationResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("location 1");
    }

    @Test
    void shouldFailFindingOneLocation_whenNotExist(){
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .get("/api/locations/1")

                //THEN
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"Location not found\"}");
    }

    @Test
    void shouldUpdateLocation() {
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(new WarehouseDTO(1L, "warehouse", 1L));
        Location location = new Location(null, "location 1", 1L);
        Location savedLocation = locationRepository.save(location);
        Long id = savedLocation.getId();

        LocationResponseDTO response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("{\"id\":" +id+",\"name\":\"B3\"}")

                //WHEN
                .when()
                .put("/api/locations")


                //THEN
                .then()
                .statusCode(200)
                .extract().as(LocationResponseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("B3");
    }
}
