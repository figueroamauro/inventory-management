package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(CategoryDTO dto) {
        Category category = new Category(dto.id(),dto.name());
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
}
