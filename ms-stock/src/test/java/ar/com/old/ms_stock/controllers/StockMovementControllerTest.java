package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.exceptions.LocationConflictException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.exceptions.ProductConflictException;
import ar.com.old.ms_stock.services.StockMovementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StockMovementController.class)
@ActiveProfiles("test")
class StockMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StockMovementService stockMovementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private StockMovementDTO dto;
    private Location location;
    private StockEntry stockEntry;
    private StockMovement stockMovement;

    @BeforeEach
    void init() {
        dto = new StockMovementDTO(MovementType.IN, 100, "", 1L, 1L);
        location = new Location(1L, "B2", 1L);
        stockEntry = new StockEntry(20, 1L, 1L);
        stockMovement = new StockMovement(1L, MovementType.IN, 100, "", location, stockEntry);
    }


    @Test
    void shouldCreateMovement_status200() throws Exception {
        //GIVEN

        when(stockMovementService.create(dto)).thenReturn(stockMovement);

        //WHEN
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    void shouldFailCreatingMovement_whenLocationIsNotExists_status409() throws Exception {
        //GIVEN
        StockMovementDTO dto = new StockMovementDTO(MovementType.IN, 100, "", 1L, 1L);
        when(stockMovementService.create(dto)).thenThrow(new LocationConflictException("Location not found"));

        //WHEN
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Location not found"));
    }

    @Test
    void shouldFailCreatingMovement_whenProductIsNotExists_status409() throws Exception {
        //GIVEN
        StockMovementDTO dto = new StockMovementDTO(MovementType.IN, 100, "", 1L, 1L);
        when(stockMovementService.create(dto)).thenThrow(new ProductConflictException("Product not found"));

        //WHEN
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Product not found"));
    }

    @Test
    void shouldFindAllMovements_status200() throws Exception {
        //GIVEN
        List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
        Page<StockMovement> page = new PageImpl<>(list, Pageable.unpaged(), list.size());
        when(stockMovementService.findAll(any(Pageable.class))).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldFindAllMovementsByLocationId_status200() throws Exception {
        //GIVEN
        List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
        Page<StockMovement> page = new PageImpl<>(list, Pageable.unpaged(), list.size());
        when(stockMovementService.findAllByLocationId(any(Pageable.class),anyLong())).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/movements?locationId=1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldFindAllMovementsByProductId_status200() throws Exception {
        //GIVEN
        List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
        Page<StockMovement> page = new PageImpl<>(list, Pageable.unpaged(), list.size());
        when(stockMovementService.findAllByProductId(any(Pageable.class),anyLong())).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/movements?productId=1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldFindAllMovementsByProductIdAndLocationId_status200() throws Exception {
        //GIVEN
        List<StockMovement> list = List.of(stockMovement, stockMovement, stockMovement);
        Page<StockMovement> page = new PageImpl<>(list, Pageable.unpaged(), list.size());
        when(stockMovementService.findAllByLocationIdAndProductId(any(Pageable.class),anyLong(),anyLong())).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/movements?productId=1&locationId=1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }
}