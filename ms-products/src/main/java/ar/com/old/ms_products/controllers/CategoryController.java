package ar.com.old.ms_products.controllers;

import ar.com.old.ms_products.dto.CategoryDTO;
import ar.com.old.ms_products.entities.Category;
import ar.com.old.ms_products.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Categorias", description = "Gestión de categorias: creación, eliminación y consulta de información")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private static final String DEFAULT_PAGE = "{\"page\": 0, \"size\": 10, \"sort\": \"name\"}";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @ApiResponse(responseCode = "201")
    @Operation(summary = "crear categoria")
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.create(categoryDTO);

        return ResponseEntity.created(URI.create("/api/categories/" + category.getId())).body(category);
    }


    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener lista de categorias")
    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(sort = "name")
                                         @Schema(example = DEFAULT_PAGE)
                                         Pageable pageable, PagedResourcesAssembler<Category> assembler) {
        Page<Category> page = categoryService.findAll(pageable);

        return ResponseEntity.ok(assembler.toModel(page));
    }

    @ApiResponse(responseCode = "200")
    @Operation(summary = "Obtener categoria por id")
    @GetMapping("/{id}")
    public ResponseEntity<Category> findOne(@PathVariable Long id) {
        Category category = categoryService.findOne(id);
        return ResponseEntity.ok(category);
    }

    @ApiResponse(responseCode = "204")
    @Operation(summary = "Eliminar una categoria por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
