package ar.com.old.ms_products;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public PagedModel<?> getAll(@PageableDefault(sort = "name") Pageable pageable, PagedResourcesAssembler<Category> assembler) {
        Page<Category> page = categoryService.findAll(pageable);

        return assembler.toModel(page);
    }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.create(categoryDTO);

        return ResponseEntity.created(URI.create("/api/categories/" + category.getId())).body(category);
    }
}
