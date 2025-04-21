package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClient;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.ConnectionFeignException;
import ar.com.old.ms_products.exceptions.ExistingCategoryException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    UserClient userClient;
    @Mock
    WarehouseRepository warehouseRepository;
    private Category category;
    private CategoryDTO dto;


    @BeforeEach
    void init() {
        category = new Category(1L, "Electro", new Warehouse(1L, "Central", 1L));
        dto = new CategoryDTO(null, "Electro");
    }

    @Nested
    class Create {


        @Test
        void shouldCreateCategory_whenDTOHasValidParams() {
            //GIVEN
            when(categoryRepository.findByNameAndWarehouseId("Electro",1L)).thenReturn(Optional.empty());
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            when(userClient.findOne(1L)).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));

            //WHEN
            Category result = categoryService.create(dto);

            //THEN
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals("Electro", result.getName());

            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void shouldThrowExceptionCreatingCategory_whenNameAlreadyExist() {
            //GIVEN
            when(userClient.findOne(1L)).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));
            when(categoryRepository.findByNameAndWarehouseId("Electro",1L)).thenReturn(Optional.of(category));

            //WHEN
            Executable executable = () -> categoryService.create(dto);

            //THEN
            ExistingCategoryException e = assertThrows(ExistingCategoryException.class, executable);
            assertEquals("Category already exist", e.getMessage());

            verify(categoryRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionCreatingCategory_whenDTOisNUll() {
            //WHEN
            Executable executable = () -> categoryService.create(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("DTO can not be null", e.getMessage());

            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        void shouldThrowExceptionCreatingCategory_whenUserNotFound() {
            //GIVEN
            when(userClient.findOne(1L)).thenReturn(null);

            //WHEN
            Executable executable = () -> categoryService.create(dto);

            //THEN
            ConnectionFeignException e = assertThrows(ConnectionFeignException.class, executable);
            assertEquals("Can not connect to another service", e.getMessage());

            verify(categoryRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionCreatingCategory_whenWarehouseNotFound() {
            //GIVEN
            when(userClient.findOne(1L)).thenReturn(new UserDTO(1L, "username", "email@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.empty());

            //WHEN
            Executable executable = () -> categoryService.create(dto);

            //THEN
            WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
            assertEquals("Warehouse not found", e.getMessage());

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    class FindOne {
        @Test
        void shouldFindCategoryById(){
            //GIVEN
            when(userClient.findOne(1L)).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));

            when(categoryRepository.findByIdAndWarehouseId(1L,1L)).thenReturn(Optional.ofNullable(category));

            //WHEN
            Category result = categoryService.findOne(1L);

            //THEN
            assertNotNull(result);
            assertEquals("Electro", result.getName());
        }

        @Test
        void shouldThrowExceptionFindingCategory_whenIdIsNull(){
            //WHEN
            Executable executable = () -> categoryService.findOne(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Id can not be null", e.getMessage());

            verify(categoryRepository, never()).findOne(any());
        }

        @Test
        void shouldThrowExceptionFindingCategory_whenCategoryNotFound(){
            //GIVEN
            when(userClient.findOne(1L)).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));

            when(categoryRepository.findByIdAndWarehouseId(1L,1L)).thenReturn(Optional.empty());

            //WHEN
            Executable executable = () -> categoryService.findOne(1L);

            //THEN
            CategoryNotFoundException e = assertThrows(CategoryNotFoundException.class, executable);
            assertEquals("Category not found", e.getMessage());

            verify(categoryRepository).findByIdAndWarehouseId(1L,1L);
        }
    }
}
