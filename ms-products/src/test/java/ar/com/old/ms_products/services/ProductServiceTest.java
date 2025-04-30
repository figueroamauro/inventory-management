package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.ConnectionFeignException;
import ar.com.old.ms_products.exceptions.ProductAlreadyExistException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
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
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private UserClientService clientService;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    private Warehouse warehouse;
    private Category category;
    private ProductDTO dto;
    private Product product;

    @BeforeEach
    void init() {
        warehouse = new Warehouse(1L, "warehouse1", 1L);
        category = new Category(1L, "category1", warehouse);
        dto = new ProductDTO("product1", "product description", 100.00, 1L);
        product = new Product(1L, "product1", "product description", 100.00, category, warehouse);

    }

    @Nested
    class Create {


        @Test
        void shouldCreateProduct() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(warehouse));
            when(categoryRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            //WHEN
            Product result = productService.create(dto);

            //THEN
            assertNotNull(result);
            assertEquals("product1", result.getName());
            assertEquals("product description", result.getDescription());
            assertEquals(100.00, result.getPrice());
            assertEquals(1L, result.getCategory().getId());
            assertEquals(1L, result.getWarehouse().getId());
            assertNotNull(result.getCreatedAt());

            verify(clientService).getUser();
            verify(warehouseRepository).findByUserId(anyLong());
            verify(categoryRepository).findByIdAndWarehouseId(anyLong(), anyLong());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void shouldFailCreatingProduct_whenUserNotFound() {
            //GIVEN
            when(clientService.getUser())
                    .thenThrow(new ConnectionFeignException("Can not connect to another service, verify you current token"));

            //WHEN
            Executable executable = () -> productService.create(dto);

            //THEN
            ConnectionFeignException e = assertThrows(ConnectionFeignException.class, executable);
            assertEquals("Can not connect to another service, verify you current token", e.getMessage());

            verify(clientService).getUser();
            verify(warehouseRepository, never()).findByUserId(anyLong());
        }

        @Test
        void shouldFailCreatingProduct_whenWarehouseNotFound() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));

            //WHEN
            Executable executable = () -> productService.create(dto);

            //THEN
            WarehouseNotFoundException e = assertThrows(WarehouseNotFoundException.class, executable);
            assertEquals("Warehouse not found", e.getMessage());

            verify(clientService).getUser();
            verify(warehouseRepository).findByUserId(anyLong());
        }

        @Test
        void shouldFailCreatingProduct_whenCategoryNotFound() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(warehouse));

            //WHEN
            Executable executable = () -> productService.create(dto);

            //THEN
            CategoryNotFoundException e = assertThrows(CategoryNotFoundException.class, executable);
            assertEquals("Category not found", e.getMessage());

            verify(clientService).getUser();
            verify(warehouseRepository).findByUserId(anyLong());
        }

        @Test
        void shouldFailCreatingProduct_whenProductAlreadyExist() {
            //GIVEN
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(warehouse));
            when(categoryRepository.findByIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(category));
            when(productRepository.findByNameAndWarehouseId("product1", 1L)).thenReturn(Optional.ofNullable(product));

            //WHEN
            Executable executable = () -> productService.create(dto);

            //THEN
            ProductAlreadyExistException e = assertThrows(ProductAlreadyExistException.class, executable);
            assertEquals("Product already exist", e.getMessage());

            verify(clientService).getUser();
            verify(warehouseRepository).findByUserId(anyLong());
        }
    }

    @Nested
    class FindAll {
        
        @Test
        void shouldFindAllProductsForTheCurrentUser(){
            //GIVEN
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> list = List.of(product, product, product);
            Page<Product> page = new PageImpl<>(list, pageable, list.size());
            when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));
            when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(warehouse));
            when(productRepository.findAllByWarehouseId(pageable,1L)).thenReturn(page);

            //WHEN
            Page<Product> result = productService.findAll(pageable);

            //THEN
            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
        }
    }
}
