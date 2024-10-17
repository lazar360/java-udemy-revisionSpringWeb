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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

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
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        /*Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        */
        Category category = categoryRepository.findById(categoryId)
                .orElse(categoryRepository.save(new Category( "No category")));
        Product product = modelMapper.map(productDTO, Product.class);
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
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productToUpdate = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        if (!Objects.equals(null, product.getProductName())) productToUpdate.setProductName(product.getProductName());
        if (!Objects.equals(null, product.getDescription())) productToUpdate.setDescription(product.getDescription());
        if (!Objects.equals(null, product.getQuantity()))productToUpdate.setQuantity(product.getQuantity());
        if (!Objects.equals(0.00, product.getDiscount())) productToUpdate.setDiscount(product.getDiscount());
        if (!Objects.equals(0.00, product.getPrice())) productToUpdate.setPrice(product.getPrice());
        if (!Objects.equals(0.00, product.getSpecialPrice())) productToUpdate.setSpecialPrice(product.getSpecialPrice());

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
                .orElseThrow(()->new ResourceNotFoundException("Product", "product", productId));

        // upload the image


        // get the filename of uploaded image
        String path = "images/";
        String fileName = uploadImage(path, image);

        // updating the new file name to the product
        productFromDb.setImage(fileName);

        // save the updatedProduct
        Product updatedProduct = productRepository.save(productFromDb);

        // return DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile file) throws IOException {
        // file names of current / original file
        String originalFileName = file.getOriginalFilename();

        //  generate a unique file name
        String randomId = UUID.randomUUID().toString();
        assert originalFileName != null;
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        // check if path exist and create
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();

        // upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
