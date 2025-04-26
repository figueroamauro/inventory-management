package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.WarehouseDTO;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.services.WarehouseServiceImpl;
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

@WebMvcTest(WarehouseController.class)
@ActiveProfiles("test")
class WarehouseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private WarehouseServiceImpl warehouseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateWarehouse_status201() throws Exception {
        //GIVEN
        WarehouseDTO dto = new WarehouseDTO(1L, "deposito");
        when(warehouseService.create(dto)).thenReturn(new Warehouse(1L, "deposito", 1L));

        //WHEN
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("deposito"));
    }
}