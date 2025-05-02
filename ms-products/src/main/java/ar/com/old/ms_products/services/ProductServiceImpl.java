package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClientService;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductUpdateDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Product;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.CategoryNotFoundException;
import ar.com.old.ms_products.exceptions.ProductAlreadyExistException;
import ar.com.old.ms_products.exceptions.ProductNotFoundException;
import ar.com.old.ms_products.exceptions.WarehouseNotFoundException;
import ar.com.old.ms_products.repositories.CategoryRepository;
import ar.com.old.ms_products.repositories.ProductRepository;
import ar.com.old.ms_products.repositories.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class ProductServiceImpl implements ProductService {

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
        validateNull(dto, "DTO can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO.id());
        Category category = getCategory(dto.categoryId(), warehouse.getId());

        validateExistingProduct(dto.name(), warehouse.getId());
        Product product = new Product(null, dto.name(), dto.description(),
                dto.price(), category, warehouse);

        return productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        validateNull(pageable, "Pageable can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO.id());

        return productRepository.findAllByWarehouseId(pageable, warehouse.getId());
    }

    @Override
    public Product findOne(Long id) {
        validateNull(id, "Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO.id());

        return productRepository.findByIdAndWarehouseId(id, warehouse.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    @Transactional
    public Product update(ProductUpdateDTO dto) {
        validateNull(dto, "DTO can not be null");
        validateNull(dto.id(), "Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO.id());
        Category category = getCategory(dto.categoryId(), warehouse.getId());

        Product product = productRepository.findByIdAndWarehouseId(dto.id(), warehouse.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        validateExistingProduct(dto.name(), warehouse.getId());

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);

        return productRepository.save(product);

    }

    @Override
    public void delete(Long id) {
        validateNull(id, "Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO.id());

        productRepository.findByIdAndWarehouseId(id, warehouse.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.deleteById(id);
    }


    private Category getCategory(Long categoryId, Long warehouseId) {
        return categoryRepository.findByIdAndWarehouseId(categoryId, warehouseId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    private Warehouse getWarehouse(Long id) {
        return warehouseRepository.findByUserId(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
    }

    private void validateExistingProduct(String name, Long warehouseId) {
        Optional<Product> productOpt = productRepository.findByNameAndWarehouseId(name, warehouseId);
        if (productOpt.isPresent()) {
            throw new ProductAlreadyExistException("Product already exist");
        }
    }

    private void validateNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
