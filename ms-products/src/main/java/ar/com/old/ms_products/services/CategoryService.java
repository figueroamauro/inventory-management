package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    Category create(CategoryDTO dto);

    Category findOne(Long id);

    List<Category> findAll();

    void delete(Long id);
}
