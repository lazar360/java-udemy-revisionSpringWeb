package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    void createCategory(Category category);

    String deleteCategory(Long categoryId);
}
