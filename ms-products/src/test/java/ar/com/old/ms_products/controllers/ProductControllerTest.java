package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductUpdateDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.ProductAlreadyExistException;
import ar.com.old.ms_products.exceptions.ProductNotFoundException;
import ar.com.old.ms_products.services.ProductServiceImpl;
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


@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductServiceImpl productService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ProductDTO productDTO;
    private Product product;
    private ProductUpdateDTO updateDTO;

    @BeforeEach
    void init() {
        productDTO = new ProductDTO("product", "description", 100.00, 1L);
        Warehouse warehouse = new Warehouse(1L, "warehouse", 1L);
        Category category = new Category(1L, "category", warehouse);
        product = new Product(1L, "product", "description", 100.00, category, warehouse);
        updateDTO = new ProductUpdateDTO(1L, "product", "description", 100.00, 1L);
    }

    @Test
    void shouldCreateProduct_status201() throws Exception {
        //GIVEN
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
    void shouldFailCreatingProduct_whenAlreadyExist_status409() throws Exception {
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

    @Test
    void shouldFindAllProducts_status200() throws Exception {
        //GIVEN
        List<Product> list = List.of(product, product, product);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<Product>(list, pageable, list.size());
        when(productService.findAll(any(Pageable.class))).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded.productResponseDTOList[0].name").value("product"));
    }

    @Test
    void shouldFindOneProduct_status200() throws Exception {
        //GIVEN
        when(productService.findOne(1L)).thenReturn(product);

        //WHEN
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("product"));
    }

    @Test
    void shouldFailFindingOneProduct_whenNotFound_status404() throws Exception {
        //GIVEN
        when(productService.findOne(1L)).thenThrow(new ProductNotFoundException("Product not found"));

        //WHEN
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Product not found"));
    }

    @Test
    void shouldUpdateProduct_status200() throws Exception {
        //GIVEN
        when(productService.update(updateDTO)).thenReturn(product);

        //WHEN
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("product"));
    }

    @Test
    void shouldFailUpdatingProduct_whenNotFound_status404() throws Exception {
        //GIVEN
        when(productService.update(updateDTO)).thenThrow(new ProductNotFoundException("Product not found"));

        //WHEN
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Product not found"));
    }

    @Test
    void shouldDeleteProduct_status204() throws Exception {
        //GIVEN

        //WHEN
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNoContent());
    }

}