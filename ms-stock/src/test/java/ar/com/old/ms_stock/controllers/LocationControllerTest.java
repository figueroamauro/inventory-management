package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.services.LocationServiceImpl;
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

@WebMvcTest(LocationController.class)
@ActiveProfiles("test")
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LocationServiceImpl locationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateLocation_return201() throws Exception {
        //GIVEN
        LocationDTO dto = new LocationDTO(1L, "B2");
        Location location = new Location(1L, "B2", 1L);
        when(locationService.create(dto)).thenReturn(location);

        //WHEN
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("B2"));
    }

    @Test
    void shouldFailCreatingLocation_whenAlreadyExist_return409() throws Exception {
        //GIVEN
        LocationDTO dto = new LocationDTO(1L, "B2");
        when(locationService.create(dto)).thenThrow(new LocationAlreadyExistException("Location already exist"));

        //WHEN
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Location already exist"));
    }
}