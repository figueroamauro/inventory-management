package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;

import java.util.List;

public class CategoryServiceImpl implements CategoryService{
    @Override
    public Category create(CategoryDTO dto) {
        return null;
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
