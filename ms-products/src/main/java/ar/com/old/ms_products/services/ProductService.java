package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.dto.ProductUpdateDTO;
import ar.com.old.ms_products.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    Product create(ProductDTO dto);

    Page<Product> findAll(Pageable pageable);

    Product findOne(Long id);

    Product update(ProductUpdateDTO dto);

    void delete(Long id);
}
