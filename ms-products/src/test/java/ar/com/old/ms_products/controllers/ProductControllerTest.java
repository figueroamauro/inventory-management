package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.ProductAlreadyExistException;
import ar.com.old.ms_products.services.ProductServiceImpl;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductServiceImpl productService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private ProductDTO productDTO;

    @BeforeEach
    void init() {
        productDTO = new ProductDTO("product", "description", 100.00, 1L);
    }

    @Test
    void shouldCreateProduct_status201() throws Exception {
        //GIVEN
        Warehouse warehouse = new Warehouse(1L, "warehouse", 1L);
        Category category = new Category(1L, "category", warehouse);
        Product product = new Product(1L, "product", "description", 100.00, category, warehouse);
        when(productService.create(productDTO)).thenReturn(product);

        //WHEN
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("product"));
    }

    @Test
    void shouldFailCreatingProduct_whenAlreadyExist_status201() throws Exception {
        //GIVEN
        when(productService.create(productDTO))
                .thenThrow(new ProductAlreadyExistException("Product already exist"));

        //WHEN
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Product already exist"));
    }
}