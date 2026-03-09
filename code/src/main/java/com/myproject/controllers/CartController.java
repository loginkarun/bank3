package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddToCartRequest request) {
        CartDTO cart = cartService.addItemToCart(userId, request);
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message("Item added to cart successfully")
                .data(cart)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @RequestHeader("X-User-Id") String userId) {
        CartDTO cart = cartService.getCart(userId);
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message("Cart retrieved successfully")
                .data(cart)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateCartRequest request) {
        CartDTO cart = cartService.updateCartItem(userId, request);
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message("Cart item updated successfully")
                .data(cart)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeCartItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String itemId) {
        CartDTO cart = cartService.removeCartItem(userId, itemId);
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message("Item removed from cart successfully")
                .data(cart)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @RequestHeader("X-User-Id") String userId) {
        cartService.clearCart(userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Cart cleared successfully")
                .data("Cart has been cleared")
                .build();
        return ResponseEntity.ok(response);
    }
}
