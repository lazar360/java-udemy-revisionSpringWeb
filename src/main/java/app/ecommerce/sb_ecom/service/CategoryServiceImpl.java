package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.exceptions.APIException;
import app.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.payload.CategoryDTO;
import app.ecommerce.sb_ecom.payload.CategoryResponse;
import app.ecommerce.sb_ecom.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize) {

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

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
    public CategoryDTO deleteCategory(Long categoryId) {
        Category deletedCategory = categoryRepository.findById(categoryId).map(existingCategory -> {
            categoryRepository.deleteById(categoryId);
            return existingCategory;
        }).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        return modelMapper.map(deletedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        categoryRepository.findById(categoryId)
                .map(existingCategory -> {
                    category.setCategoryId(categoryId);
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

          return categoryDTO;
    }
}
