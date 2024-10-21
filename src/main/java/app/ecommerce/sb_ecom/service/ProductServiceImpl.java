package app.ecommerce.sb_ecom.service;

import app.ecommerce.sb_ecom.exceptions.APIException;
import app.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import app.ecommerce.sb_ecom.model.Category;
import app.ecommerce.sb_ecom.model.Product;
import app.ecommerce.sb_ecom.payload.ProductDTO;
import app.ecommerce.sb_ecom.payload.ProductResponse;
import app.ecommerce.sb_ecom.repositories.CategoryRepository;
import app.ecommerce.sb_ecom.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    private final FileService fileService;

    @Value("${project.image}")
    private String path;


    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Check if product already present or not
        boolean isProductNotPresent = true;
        if (!category.getProducts().isEmpty()) {
            isProductNotPresent = category.getProducts().stream()
                    .noneMatch(product -> Objects.equals(productDTO.getProductName(), product.getProductName()));
        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            product.setSpecialPrice(product.getPrice() * (1 - product.getDiscount() * 0.01));

            return modelMapper.map(productRepository.save(product), ProductDTO.class);
        } else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return getProductResponse(productRepository.findAll(getPageable(pageNumber, pageSize, sortBy, sortOrder)));
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        return getProductResponse(productRepository.findByCategoryOrderByPriceAsc(category, getPageable(pageNumber, pageSize, sortBy, sortOrder)));
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return getProductResponse(productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', getPageable(pageNumber, pageSize, sortBy, sortOrder)));
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productToUpdate = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        if (!Objects.equals(null, product.getProductName())) productToUpdate.setProductName(product.getProductName());
        if (!Objects.equals(null, product.getDescription())) productToUpdate.setDescription(product.getDescription());
        if (!Objects.equals(null, product.getQuantity())) productToUpdate.setQuantity(product.getQuantity());
        if (!Objects.equals(0.00, product.getDiscount())) productToUpdate.setDiscount(product.getDiscount());
        if (!Objects.equals(0.00, product.getPrice())) productToUpdate.setPrice(product.getPrice());
        if (!Objects.equals(0.00, product.getSpecialPrice()))
            productToUpdate.setSpecialPrice(product.getSpecialPrice());

        return modelMapper.map(productRepository.save(productToUpdate), ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productToDelete = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.deleteById(productId);
        return modelMapper.map(productToDelete, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        // get the product from db
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "product", productId));

        // get the filename of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // updating the new file name to the product
        productFromDb.setImage(fileName);

        // save the updatedProduct
        Product updatedProduct = productRepository.save(productFromDb);

        // return DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private Pageable getPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private ProductResponse getProductResponse(Page<Product> pageProducts) {

        List<Product> products = pageProducts.getContent();

        if (products.isEmpty()) {
            throw new APIException("No product in database");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElement(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }
}
