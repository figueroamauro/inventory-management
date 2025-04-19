package ar.com.old.ms_products.services;

import ar.com.old.ms_products.clients.UserClient;
import ar.com.old.ms_products.clients.dto.UserDTO;
import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.entities.Warehouse;
import ar.com.old.ms_products.exceptions.ExistingCategoryException;
import ar.com.old.ms_products.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final UserClient userClient;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserClient userClient) {
        this.categoryRepository = categoryRepository;
        this.userClient = userClient;
    }

    @Override
    public Category create(CategoryDTO dto) {
        validateNull(dto, "DTO can not be null");
        checkExistingCategory(dto);
        UserDTO userDTO = userClient.findOne(1L);
        Category category = new Category(dto.id(), dto.name(),new Warehouse(1L,"Central",1L));
        return categoryRepository.save(category);

    }

    @Override
    public Category findOne(Long id) {
        return null;
    }

    @Override
    public List<Category> findAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }


    private void checkExistingCategory(CategoryDTO dto) {
        Optional<Category> categoryOpt = categoryRepository.findByName(dto.name());
        if (categoryOpt.isPresent()) {
            throw new ExistingCategoryException("Category already exist");
        }
    }

    private void validateNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
