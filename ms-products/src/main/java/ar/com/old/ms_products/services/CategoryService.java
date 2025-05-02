package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface CategoryService {

    Category create(CategoryDTO dto);

    Page<Category> findAll(Pageable pageable);

    Category findOne(Long id);

    void delete(Long id);
}
