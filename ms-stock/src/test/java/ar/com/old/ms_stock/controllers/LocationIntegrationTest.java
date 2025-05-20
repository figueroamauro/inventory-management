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
import java.util.Optional;

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

    private Location location;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void cleanup() {
        locationRepository.deleteAll();
        location = new Location(null, "B1", 1L);
        warehouseDTO = new WarehouseDTO(1L, "warehouse", 1L);
    }

    @Test
    void shouldCreateLocation() {
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

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
        locationRepository.saveAll(List.of(location));
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

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
        assertThat(content.size()).isEqualTo(1);
    }

    @Test
    void shouldFailFindingAllLocations_whenNotExist(){
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

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
        Location savedLocation = locationRepository.save(location);
        Long id = savedLocation.getId();
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

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
        assertThat(response.name()).isEqualTo("B1");
    }

    @Test
    void shouldFailFindingOneLocation_whenNotExist(){
        //GIVEN
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

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
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);
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

    @Test
    void shouldFailUpdatingLocation_whenDTOHasNullId() {
        //GIVEN

        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"B1\"}")

                //WHEN
                .when()
                .put("/api/locations")


                //THEN
                .then()
                .statusCode(400)
                .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.asString()).isEqualTo("{\"error\":\"Id can not be null\"}");
    }

    @Test
    void shouldDeleteLocation(){
        //GIVEN
        Location savedLocation = locationRepository.save(location);
        Long id = savedLocation.getId();
        Mockito.when(productsClientService.getWarehouse()).thenReturn(warehouseDTO);

        given()
                .port(port)
                .contentType(ContentType.JSON)

                //WHEN
                .when()
                .delete("/api/locations/" + id)

                //THEN
                .then()
                .statusCode(204);

        Optional<Location> deletedLocation = locationRepository.findByIdAndWarehouseIdAndActiveTrue(id,1L);
        assertThat(deletedLocation).isEmpty();
    }
}
