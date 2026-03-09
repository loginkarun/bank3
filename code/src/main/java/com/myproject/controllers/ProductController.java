package com.myproject.controllers;

import com.myproject.models.dtos.ApiResponse;
import com.myproject.models.dtos.ProductDTO;
import com.myproject.services.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        ApiResponse<List<ProductDTO>> response = ApiResponse.<List<ProductDTO>>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable String productId) {
        ProductDTO product = productService.getProductById(productId);
        ApiResponse<ProductDTO> response = ApiResponse.<ProductDTO>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }
}
