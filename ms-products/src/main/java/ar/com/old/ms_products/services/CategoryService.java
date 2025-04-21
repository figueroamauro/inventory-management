package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    Category create(CategoryDTO dto);

    Category findOne(Long id);

    Page<Category> findAll(Pageable pageable);

    void delete(Long id);
}
