package com.myproject.services;

import com.myproject.exceptions.ProductNotFoundException;
import com.myproject.models.dtos.ProductDTO;
import com.myproject.models.entities.Product;
import com.myproject.models.repositories.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @PostConstruct
    public void initSampleProducts() {
        if (productRepository.count() == 0) {
            Product p1 = Product.builder()
                    .name("Laptop")
                    .price(new BigDecimal("999.99"))
                    .availableStock(10)
                    .description("High performance laptop")
                    .build();
            Product p2 = Product.builder()
                    .name("Mouse")
                    .price(new BigDecimal("29.99"))
                    .availableStock(50)
                    .description("Wireless mouse")
                    .build();
            productRepository.save(p1);
            productRepository.save(p2);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        return convertToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean productExists(String productId) {
        return productRepository.existsById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientStock(String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        return product.getAvailableStock() >= quantity;
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(product.getAvailableStock())
                .description(product.getDescription())
                .build();
    }
}
