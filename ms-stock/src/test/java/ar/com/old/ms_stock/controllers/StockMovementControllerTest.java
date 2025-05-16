package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.StockMovementDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.entities.StockMovement;
import ar.com.old.ms_stock.enums.MovementType;
import ar.com.old.ms_stock.exceptions.LocationConflictException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.services.StockMovementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void shouldCreateMovement_status200() throws Exception {
        //GIVEN
        StockMovementDTO dto = new StockMovementDTO(MovementType.IN, 100, "", 1L, 1L);
        Location location = new Location(1L, "B2", 1L);
        StockEntry stockEntry = new StockEntry(20, 1L, 1L);
        StockMovement stockMovement = new StockMovement(1L, MovementType.IN, 100, "", location, stockEntry);
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
}