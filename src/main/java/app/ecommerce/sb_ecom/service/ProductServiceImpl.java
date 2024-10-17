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

        /*Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        */
        Category category = categoryRepository.findById(categoryId)
                .orElse(categoryRepository.save(new Category( "No category")));

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

    @Override
    public ProductDTO updateProduct(Long productId, Product product) {
        Product productToUpdate = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        if (!Objects.equals(null, product.getProductName())) productToUpdate.setProductName(product.getProductName());
        if (!Objects.equals(null, product.getDescription())) productToUpdate.setDescription(product.getDescription());
        if (!Objects.equals(null, product.getQuantity()))productToUpdate.setQuantity(product.getQuantity());
        if (!Objects.equals(0.00, product.getDiscount())) productToUpdate.setDiscount(product.getDiscount());
        if (!Objects.equals(0.00, product.getPrice())) productToUpdate.setPrice(product.getPrice());
        if (!Objects.equals(0.00, product.getSpecialPrice())) productToUpdate.setSpecialPrice(product.getSpecialPrice());

        return modelMapper.map(productRepository.save(productToUpdate), ProductDTO.class);
    }
}
