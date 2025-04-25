package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClient;
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
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserClientService clientService;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserClientService clientService,
                               WarehouseRepository warehouseRepository) {
        this.categoryRepository = categoryRepository;
        this.clientService = clientService;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Category create(CategoryDTO dto) {
        validateNull(dto, "DTO can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO);

        checkExistingCategory(dto, warehouse.getId());

        Category category = new Category(dto.id(), dto.name(), warehouse);
        return categoryRepository.save(category);

    }



    @Override
    public Category findOne(Long id) {
        validateNull(id, "Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO);

        return categoryRepository.findByIdAndWarehouseId(id, warehouse.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        validateNull(pageable, "Pageable can not be null");
        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO);
        return categoryRepository.findAllByWarehouseId(pageable, warehouse.getId());
    }

    @Override
    public void delete(Long id) {
        validateNull(id, "Id can not be null");

        UserDTO userDTO = clientService.getUser();
        Warehouse warehouse = getWarehouse(userDTO);
        Category category = categoryRepository.findByIdAndWarehouseId(id, warehouse.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryRepository.deleteById(category.getId());

    }


    private void checkExistingCategory(CategoryDTO dto, Long warehouseId) {
        Optional<Category> categoryOpt = categoryRepository.findByNameAndWarehouseId(dto.name(), warehouseId);
        if (categoryOpt.isPresent()) {
            throw new ExistingCategoryException("Category already exist");
        }
    }

    private void validateNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private Warehouse getWarehouse(UserDTO userDTO) {
        if (userDTO != null) {
            return warehouseRepository.findByUserId(userDTO.id())
                    .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        }
        throw new ConnectionFeignException("Can not connect to another service");
    }



}
