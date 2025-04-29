package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public class ProductServiceImpl implements ProductService{

    private final UserClientService clientService;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(UserClientService clientService, ProductRepository productRepository, WarehouseRepository warehouseRepository, CategoryRepository categoryRepository) {
        this.clientService = clientService;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product create(ProductDTO dto) {
        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = warehouseRepository.findByUserId(userDTO.id())
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        Category category = categoryRepository.findByIdAndWarehouseId(dto.categoryId(), warehouse.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Product product = new Product(null, dto.name(), dto.description(),
                dto.price(), LocalDateTime.now(), category, warehouse);
        return productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Product findOne(Long id) {
        return null;
    }

    @Override
    public Product update(ProductDTO dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
