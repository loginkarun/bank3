package com.myproject.services.impl;

import com.myproject.models.dtos.Product;
import com.myproject.services.interfaces.ProductService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final Map<String, Product> productCatalog = new HashMap<>();
    
    public ProductServiceImpl() {
        // Initialize with sample products for demo
        productCatalog.put("PROD-12345", new Product("PROD-12345", "Wireless Mouse", 29.99, 100));
        productCatalog.put("PROD-67890", new Product("PROD-67890", "Mechanical Keyboard", 89.99, 50));
        productCatalog.put("PROD-11111", new Product("PROD-11111", "USB-C Cable", 12.99, 200));
        productCatalog.put("PROD-22222", new Product("PROD-22222", "Laptop Stand", 45.99, 75));
        productCatalog.put("PROD-33333", new Product("PROD-33333", "Webcam HD", 59.99, 30));
    }
    
    @Override
    public Optional<Product> getProductById(String productId) {
        return Optional.ofNullable(productCatalog.get(productId));
    }
    
    @Override
    public boolean isProductAvailable(String productId, Integer requestedQuantity) {
        return getProductById(productId)
            .map(product -> product.getAvailableStock() >= requestedQuantity)
            .orElse(false);
    }
}