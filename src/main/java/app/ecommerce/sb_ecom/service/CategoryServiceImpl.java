package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService{

    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        if (this.categories.removeIf(category -> Objects.equals(category.getCategoryId(), categoryId))){
        return "Category with categoryId " + categoryId + " deleted successfully";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
    }
}
