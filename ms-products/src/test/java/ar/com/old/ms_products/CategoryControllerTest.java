package ar.com.old.ms_products;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.ExistingCategoryException;
import ar.com.old.ms_products.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoryService categoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Category category;
    private CategoryDTO dto;

    @BeforeEach
    void init() {
        category = new Category(1L, "indumentaria", new Warehouse(1L, "warehouse", 1L));
        dto = new CategoryDTO(null, "indumentaria");
    }


    @Test
    void shouldFindAllCategories_status200() throws Exception {
        //GIVEN
        Page<Category> page = buildPage();
        when(categoryService.findAll(any(Pageable.class))).thenReturn(page);

        //WHEN
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldThrowExceptionFindingAllCategories_withoutPageable_status400() throws Exception {
        //WHEN
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Page must not be null"));
    }

    @Test
    void shouldFindById_status200() throws Exception {
        //GIVEN
        when(categoryService.findOne(1L)).thenReturn(category);

        //WHEN
        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.name").value("indumentaria"));
    }

    @Test
    void shouldThrowExceptionFindingById_whenCategoryIsNotPresent_status404() throws Exception {
        //GIVEN
        when(categoryService.findOne(1L)).thenThrow(new CategoryNotFoundException("Category not found"));

        //WHEN
        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Category not found"));
    }

    @Test
    void shouldCreateCategory_status201() throws Exception {
        //GIVEN
        when(categoryService.create(dto)).thenReturn(category);

        //WHEN
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("indumentaria"));
    }

    @Test
    void shouldFailCreatingCategory_status409() throws Exception {
        //GIVEN
        when(categoryService.create(dto)).thenThrow(new ExistingCategoryException("Category already exist"));

        //WHEN
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                //THEN
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Category already exist"));
    }

    @Test
    void shouldDeleteCategory_status204() throws Exception {
        //WHEN
        mockMvc.perform(delete("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowExceptionDeletingCategory_whenCategoryIsNotPresent_status404() throws Exception {
        //GIVEN
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).delete(1L);

        //WHEN
        mockMvc.perform(delete("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.error").value("Category not found"));
    }


    private @NotNull Page<Category> buildPage() {
        List<Category> list = List.of(category,category,category);
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(list, pageable, list.size());
    }
}