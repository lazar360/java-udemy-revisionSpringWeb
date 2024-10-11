package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.exceptions.APIException;
import app.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.payload.CategoryDTO;
import app.ecommerce.sb_ecom.payload.CategoryResponse;
import app.ecommerce.sb_ecom.repositories.CategoryRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) throw new APIException("no category created until now");

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        return new CategoryResponse(categoryDTOS);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if(savedCategory != null) throw new APIException("Category with the name " + category.getCategoryName() + " already exists");

        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).map(existingCategory -> {
            categoryRepository.deleteById(categoryId);
            return existingCategory;
        }).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        return "Category with categoryId: " + categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
         return categoryRepository.findById(categoryId)
                .map(existingCategory -> {
                    category.setCategoryId(categoryId);
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        }
}
