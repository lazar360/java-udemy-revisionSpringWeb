package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.payload.CategoryDTO;
import app.ecommerce.sb_ecom.payload.CategoryResponse;
import jakarta.validation.Valid;

public interface CategoryService {

    CategoryResponse getAllCategories();
    CategoryDTO createCategory(@Valid CategoryDTO categoryDTO);
    String deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
