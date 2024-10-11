package app.ecommerce.sb_ecom;

import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.repositories.CategoryRepository;
import app.ecommerce.sb_ecom.service.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO : make the tests for exceptions
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        category1 = new Category(1L, "category1");
    }

    /*@Test
    public void shouldCreateCategory() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1));
        categoryService.createCategory(category1);
        assertEquals(1, categoryService.getAllCategories().size());
    }

    @Test
    public void shouldGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1));
        assertEquals(1, categoryService.getAllCategories().size());
    }*/

    @Test
    public void shouldUpdateCategory(){
        // Arrange
        when(categoryRepository.findById(category1.getCategoryId()))
                .thenReturn(Optional.of(category1));
        category1.setCategoryName("update");
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);

        // Act
        Category updatedCategory = categoryService.updateCategory(category1, category1.getCategoryId());

        // Assert
        assertEquals("update", updatedCategory.getCategoryName());
    }

    @Test
    public void shouldDeleteCategory(){
        // Arrange
        when(categoryRepository.findById(category1.getCategoryId()))
                .thenReturn(Optional.of(category1));

        // Act
        String result = categoryService.deleteCategory(category1.getCategoryId());

        // Assert
        verify(categoryRepository, times(1)).deleteById(category1.getCategoryId());
        assertEquals("Category with categoryId: 1 deleted successfully", result);
    }
}
