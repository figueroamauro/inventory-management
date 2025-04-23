package ar.com.old.ms_products;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.ExistingCategoryException;
import ar.com.old.ms_products.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoryService categoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateCategory_status200() throws Exception {
        //GIVEN
        CategoryDTO dto = new CategoryDTO(null, "indumentaria");
        when(categoryService.create(dto)).thenReturn(new Category(1L, "indumentaria", new Warehouse(1L, "deposito", 1L)));

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
        CategoryDTO dto = new CategoryDTO(null, "indumentaria");
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
}