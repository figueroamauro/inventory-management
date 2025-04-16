package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;


    @Test
    void shouldCreateCategory_whenDTOHasValidParams(){
        //GIVEN
        CategoryDTO dto = new CategoryDTO(null, "Electro");
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(1L, "Electro"));

        //WHEN
        Category result = categoryService.create(dto);

        //THEN
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Electro", result.getName());

        verify(categoryRepository).save(any(Category.class));
    }
}