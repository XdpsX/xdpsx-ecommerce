package com.xdpsx.ecommerce.controllers;

import com.xdpsx.ecommerce.dtos.category.CategoryRequest;
import com.xdpsx.ecommerce.dtos.category.CategoryResponse;
import com.xdpsx.ecommerce.dtos.common.PageParams;
import com.xdpsx.ecommerce.dtos.common.PageResponse;
import com.xdpsx.ecommerce.dtos.product.ProductPageParams;
import com.xdpsx.ecommerce.dtos.product.ProductResponse;
import com.xdpsx.ecommerce.services.CategoryService;
import com.xdpsx.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategories(@Valid PageParams request) {
        PageResponse<CategoryResponse> categories = categoryService.getAllCategories(request);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Integer id) {
        CategoryResponse category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse createdCategory = categoryService.createCategory(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<PageResponse<ProductResponse>> getAllProductsByCategory(
            @PathVariable Integer id,
            @Valid ProductPageParams params
    ){
        PageResponse<ProductResponse> products = productService.getAllProducts(params, id);
        return ResponseEntity.ok(products);
    }
}
