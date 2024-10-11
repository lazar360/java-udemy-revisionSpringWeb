package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Category category, Long categoryId);
}
