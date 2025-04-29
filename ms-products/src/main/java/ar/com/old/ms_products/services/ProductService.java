package ar.com.old.ms_products.services;

import ar.com.old.ms_products.dto.ProductDTO;
import ar.com.old.ms_products.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {

    Product create(ProductDTO dto);

    Page<Product> findAll(Pageable pageable);

    Product findOne(Long id);

    Product update(ProductDTO dto);

    void delete(Long id);
}
