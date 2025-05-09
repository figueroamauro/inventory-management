package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.dto.LocationDTO;
import ar.com.old.ms_stock.entities.Location;
import ar.com.old.ms_stock.exceptions.LocationAlreadyExistException;
import ar.com.old.ms_stock.exceptions.LocationNotFoundException;
import ar.com.old.ms_stock.services.LocationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    private LocationDTO dto;
    private Location location;

    @BeforeEach
    void init() {
        dto = new LocationDTO(1L, "B2");
        location = new Location(1L, "B2", 1L);
    }

    @Test
    void shouldCreateLocation_return201() throws Exception {
        //GIVEN
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

    @Test
    void shouldFindAllLocations_return200() throws Exception {
        //GIVEN
        List<Location> list = List.of(location, location, location);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> page = new PageImpl<>(list, pageable, list.size());
        when(locationService.findAll(any(Pageable.class))).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldFindOneLocation_return200() throws Exception {
        //GIVEN
        when(locationService.findOne(1L)).thenReturn(location);

        //WHEN
        mockMvc.perform(get("/api/locations/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("B2"));

        verify(locationService).findOne(1L);
    }

    @Test
    void shouldFailFindingById_whenNotFound_status404() throws Exception {
        //GIVEN
        when(locationService.findOne(1L)).thenThrow(new LocationNotFoundException("Location not found"));

        //WHEN
        mockMvc.perform(get("/api/locations/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Location not found"));
    }

    @Test
    void shouldUpdateLocation_return200() throws Exception {
        //GIVEN
        when(locationService.update(dto)).thenReturn(location);

        //WHEN
        mockMvc.perform(put("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("B2"));

        verify(locationService).update(dto);
    }

    @Test
    void shouldFailUpdatingLocation_whenNotFound_return404() throws Exception {
        //GIVEN
        when(locationService.update(dto)).thenThrow(new LocationNotFoundException("Location not found"));

        //WHEN
        mockMvc.perform(put("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Location not found"));

        verify(locationService).update(dto);
    }

}