package ar.com.old.ms_stock.controllers;

import ar.com.old.ms_stock.entities.StockEntry;
import ar.com.old.ms_stock.services.StockEntryService;
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

@WebMvcTest(StockEntryController.class)
@ActiveProfiles("test")
class StockEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockEntryService stockEntryService;

    private StockEntry stockEntry;

    @BeforeEach
    void init() {
        stockEntry = new StockEntry(100, 1L, 1L);
    }


    @Test
    void shouldFindAll_status200() throws Exception {
        //GIVEN
        List<StockEntry> list = List.of(stockEntry, stockEntry, stockEntry);
        Pageable pageable = PageRequest.of(0, 10);
        Page<StockEntry> page = new PageImpl<>(list, pageable, list.size());
        when(stockEntryService.findAll(any(Pageable.class))).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/stock")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldFindOne_status200() throws Exception {
        //GIVEN
        when(stockEntryService.findOne(1L)).thenReturn(stockEntry);

        //WHEN
        mockMvc.perform(get("/api/stock/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.quantity").value(100));
    }
}