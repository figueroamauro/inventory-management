package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.WarehouseAlreadyExistException;
import ar.com.old.ms_products.services.WarehouseServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(WarehouseController.class)
@ActiveProfiles("test")
class WarehouseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private WarehouseServiceImpl warehouseService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    WarehouseDTO dto;
    Warehouse warehouse;

    @BeforeEach
    void init() {
        dto = new WarehouseDTO(1L, "deposito");
        warehouse = new Warehouse(1L, "deposito", 1L);
    }

    @Test
    void shouldCreateWarehouse_status201() throws Exception {
        //GIVEN
        when(warehouseService.create(dto)).thenReturn(warehouse);

        //WHEN
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("deposito"));
    }

    @Test
    void shouldFailCreatingWarehouse_whenAlreadyExist_status409() throws Exception {
        //GIVEN
        when(warehouseService.create(dto)).thenThrow(new WarehouseAlreadyExistException("Warehouse already exist"));

        //WHEN
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Warehouse already exist"));
    }
}