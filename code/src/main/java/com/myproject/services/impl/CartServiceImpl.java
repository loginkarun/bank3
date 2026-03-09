package com.myproject.services.impl;

import com.myproject.exceptions.CartNotFoundException;
import com.myproject.exceptions.InsufficientStockException;
import com.myproject.exceptions.ProductNotFoundException;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Cart;
import com.myproject.models.entities.CartItemEntity;
import com.myproject.models.repositories.CartRepository;
import com.myproject.services.interfaces.CartService;
import com.myproject.services.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public CartResponse addItemToCart(String userId, AddItemRequest request) {
        // Validate product exists
        Product product = productService.getProductById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + request.getProductId()));
        
        // Validate stock availability
        if (!productService.isProductAvailable(request.getProductId(), request.getQuantity())) {
            throw new InsufficientStockException("Insufficient stock for product: " + request.getProductId());
        }
        
        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart.setItems(new ArrayList<>());
                newCart.setTotals(0.0);
                return newCart;
            });
        
        // Check if item already exists in cart
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Validate new quantity against stock
            if (!productService.isProductAvailable(request.getProductId(), newQuantity)) {
                throw new InsufficientStockException("Insufficient stock for requested quantity");
            }
            
            item.setQuantity(newQuantity);
            item.calculateSubtotal();
        } else {
            // Add new item
            CartItemEntity newItem = new CartItemEntity();
            newItem.setProductId(product.getProductId());
            newItem.setName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(request.getQuantity());
            newItem.calculateSubtotal();
            cart.addItem(newItem);
        }
        
        cart.calculateTotals();
        Cart savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }
    
    @Override
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        return convertToCartResponse(cart);
    }
    
    @Override
    public CartResponse updateCartItem(String userId, UpdateItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        CartItemEntity item = cart.getItems().stream()
            .filter(i -> i.getProductId().equals(request.getProductId()))
            .findFirst()
            .orElseThrow(() -> new ProductNotFoundException("Product not found in cart: " + request.getProductId()));
        
        // Validate stock availability for new quantity
        if (!productService.isProductAvailable(request.getProductId(), request.getQuantity())) {
            throw new InsufficientStockException("Insufficient stock for requested quantity");
        }
        
        item.setQuantity(request.getQuantity());
        item.calculateSubtotal();
        cart.calculateTotals();
        
        Cart savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }
    
    @Override
    public CartResponse removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        CartItemEntity itemToRemove = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ProductNotFoundException("Product not found in cart: " + productId));
        
        cart.removeItem(itemToRemove);
        cart.calculateTotals();
        
        Cart savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }
    
    @Override
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        cart.getItems().clear();
        cart.setTotals(0.0);
        cartRepository.save(cart);
    }
    
    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotals(cart.getTotals());
        
        List<CartItem> items = cart.getItems().stream()
            .map(entity -> {
                CartItem item = new CartItem();
                item.setProductId(entity.getProductId());
                item.setName(entity.getName());
                item.setPrice(entity.getPrice());
                item.setQuantity(entity.getQuantity());
                item.setSubtotal(entity.getSubtotal());
                return item;
            })
            .collect(Collectors.toList());
        
        response.setItems(items);
        return response;
    }
}