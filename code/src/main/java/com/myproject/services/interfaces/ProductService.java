package com.myproject.services.interfaces;

import com.myproject.models.dtos.Product;

import java.util.Optional;

public interface ProductService {
    
    Optional<Product> getProductById(String productId);
    
    boolean isProductAvailable(String productId, Integer requestedQuantity);
}