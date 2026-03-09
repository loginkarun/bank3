package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "guest-user"; // Default for demo purposes
    }
    
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody AddItemRequest request) {
        String userId = getCurrentUserId();
        CartResponse response = cartService.addItemToCart(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        String userId = getCurrentUserId();
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCartItem(@Valid @RequestBody UpdateItemRequest request) {
        String userId = getCurrentUserId();
        CartResponse response = cartService.updateCartItem(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable("id") String productId) {
        String userId = getCurrentUserId();
        CartResponse response = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<SuccessResponse> clearCart() {
        String userId = getCurrentUserId();
        cartService.clearCart(userId);
        SuccessResponse response = new SuccessResponse("Cart cleared successfully");
        return ResponseEntity.ok(response);
    }
}