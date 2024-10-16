package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.model.Product;
import app.ecommerce.sb_ecom.payload.ProductDTO;
import app.ecommerce.sb_ecom.payload.ProductResponse;
import app.ecommerce.sb_ecom.repositories.CategoryRepository;
import app.ecommerce.sb_ecom.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setImage("default.png");
        product.setCategory(category);
        product.setSpecialPrice(product.getPrice() * (1 - product.getDiscount() * 0.01));

        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        return new ProductResponse(
                productRepository.findAll().stream()
                        .map(product -> modelMapper.map(product, ProductDTO.class))
                        .toList()
        );
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        return new ProductResponse(
                productRepository.findByCategoryOrderByPriceAsc(category).stream()
                        .map(product -> modelMapper.map(product, ProductDTO.class))
                        .toList()
        );
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        return new ProductResponse(
                productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%').stream()
                        .map(product -> modelMapper.map(product, ProductDTO.class))
                        .toList()
        );
    }
}
