package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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


    @Test
    void shouldCreateProduct() {
        //GIVEN
        Warehouse warehouse = new Warehouse(1L, "warehouse1", 1L);
        Category category = new Category(1L, "category1", warehouse);
        ProductDTO dto = new ProductDTO("product1", "product description", 100.00, 1L);
        Product product = new Product(1L, "product1", "product description", 100.00, LocalDateTime.now(), category, warehouse);
        when(clientService.getUser()).thenReturn(new UserDTO(1L, "user1", "email1@mail.com"));
        when(warehouseRepository.findByUserId(1L)).thenReturn(Optional.of(warehouse));
        when(categoryRepository.findByIdAndWarehouseId(1L,1L)).thenReturn(Optional.of(category));
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

    }
}