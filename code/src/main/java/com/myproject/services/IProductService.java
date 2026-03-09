package com.myproject.services;

import com.myproject.models.dtos.ProductDTO;
import java.util.List;

public interface IProductService {
    ProductDTO getProductById(String productId);
    List<ProductDTO> getAllProducts();
    boolean productExists(String productId);
    boolean hasSufficientStock(String productId, Integer quantity);
}
