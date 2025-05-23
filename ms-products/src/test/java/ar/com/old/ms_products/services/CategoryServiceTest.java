package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    UserClientService userClientService;
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
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
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
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
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
        void shouldThrowExceptionCreatingCategory_whenUserServiceNoConnect() {
            //GIVEN
            when(userClientService.getUser())
                    .thenThrow(new ConnectionFeignException("Can not connect to another service"));

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
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "username", "email@mail.com"));
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
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
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
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
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

    @Nested
    class FindAll {

        @Test
        void shouldReturnPageOfCategories_withCurrentWarehouse(){
            //GIVEN
            Pageable pageable = PageRequest.of(0, 10);
            List<Category> list = List.of(category, category, category, category, category);
            Page<Category> page = new PageImpl<>(list, pageable, list.size());
            when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
            when(categoryRepository.findAllByWarehouseId(pageable, 1L)).thenReturn(page);
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));

            //WHEN
            Page<Category> result = categoryService.findAll(pageable);

            //THEN
            assertNotNull(result);
            assertEquals(5, result.getTotalElements());

            verify(categoryRepository).findAllByWarehouseId(any(), anyLong());
        }

        @Test
        void shouldThrowExceptionFindingAllCategories_whenPageableIsNull(){
            //WHEN
            Executable executable = () -> categoryService.findAll(null);

            //THEN
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("Pageable can not be null", e.getMessage());
        }


        @Nested
        class Delete {

            @Test
            void shouldDeleteById_whenUserIsOwner(){
                //GIVEN
                when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
                when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));
                when(categoryRepository.findByIdAndWarehouseId(1L,1L)).thenReturn(Optional.ofNullable(category));

                //WHEN
                categoryService.delete(1L);

                //THEN
                verify(categoryRepository).deleteById(1L);
            }

            @Test
            void shouldThrowExceptionDeletingCategory_whenIdIsNull(){
                //WHEN
                Executable executable = () -> categoryService.delete(null);

                //THEN
                IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
                assertEquals("Id can not be null", e.getMessage());

                verify(categoryRepository, never()).findOne(any());
            }

            @Test
            void shouldThrowExceptionDeletingCategory_whenCategoryOrWarehouseNotFound(){
                //GIVEN
                when(userClientService.getUser()).thenReturn(new UserDTO(1L, "test", "test@mail.com"));
                when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(new Warehouse(1L, "Central", 1L)));
                when(categoryRepository.findByIdAndWarehouseId(1L,1L)).thenReturn(Optional.empty());

                //WHEN
                Executable executable = () -> categoryService.delete(1L);

                //THEN
                CategoryNotFoundException e = assertThrows(CategoryNotFoundException.class, executable);
                assertEquals("Category not found", e.getMessage());

                verify(categoryRepository, never()).findOne(any());
            }
        }
    }
}
