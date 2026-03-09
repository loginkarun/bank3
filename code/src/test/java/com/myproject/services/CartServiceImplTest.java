package com.myproject.services;

import com.myproject.exceptions.CartNotFoundException;
import com.myproject.exceptions.InsufficientStockException;
import com.myproject.exceptions.ProductNotFoundException;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Cart;
import com.myproject.models.entities.CartItemEntity;
import com.myproject.models.repositories.CartRepository;
import com.myproject.services.impl.CartServiceImpl;
import com.myproject.services.interfaces.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private CartServiceImpl cartService;
    
    private Cart cart;
    private Product product;
    private AddItemRequest addItemRequest;
    
    @BeforeEach
    void setUp() {
        product = new Product("PROD-12345", "Wireless Mouse", 29.99, 100);
        addItemRequest = new AddItemRequest("PROD-12345", 2);
        
        cart = new Cart();
        cart.setId("CART-001");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());
        cart.setTotals(0.0);
    }
    
    @Test
    void testAddItemToCart_NewCart_Success() {
        when(productService.getProductById("PROD-12345")).thenReturn(Optional.of(product));
        when(productService.isProductAvailable("PROD-12345", 2)).thenReturn(true);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        CartResponse response = cartService.addItemToCart("user123", addItemRequest);
        
        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    void testAddItemToCart_ProductNotFound() {
        when(productService.getProductById("PROD-12345")).thenReturn(Optional.empty());
        
        assertThrows(ProductNotFoundException.class, () -> {
            cartService.addItemToCart("user123", addItemRequest);
        });
    }
    
    @Test
    void testAddItemToCart_InsufficientStock() {
        when(productService.getProductById("PROD-12345")).thenReturn(Optional.of(product));
        when(productService.isProductAvailable("PROD-12345", 2)).thenReturn(false);
        
        assertThrows(InsufficientStockException.class, () -> {
            cartService.addItemToCart("user123", addItemRequest);
        });
    }
    
    @Test
    void testGetCart_Success() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        
        CartResponse response = cartService.getCart("user123");
        
        assertNotNull(response);
        assertEquals("CART-001", response.getId());
        assertEquals("user123", response.getUserId());
    }
    
    @Test
    void testGetCart_CartNotFound() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        
        assertThrows(CartNotFoundException.class, () -> {
            cartService.getCart("user123");
        });
    }
    
    @Test
    void testUpdateCartItem_Success() {
        CartItemEntity item = new CartItemEntity();
        item.setProductId("PROD-12345");
        item.setName("Wireless Mouse");
        item.setPrice(29.99);
        item.setQuantity(2);
        item.calculateSubtotal();
        cart.getItems().add(item);
        
        UpdateItemRequest updateRequest = new UpdateItemRequest("PROD-12345", 5);
        
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(productService.isProductAvailable("PROD-12345", 5)).thenReturn(true);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        CartResponse response = cartService.updateCartItem("user123", updateRequest);
        
        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    void testRemoveItemFromCart_Success() {
        CartItemEntity item = new CartItemEntity();
        item.setProductId("PROD-12345");
        item.setName("Wireless Mouse");
        item.setPrice(29.99);
        item.setQuantity(2);
        item.calculateSubtotal();
        item.setCart(cart);
        cart.getItems().add(item);
        
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        CartResponse response = cartService.removeItemFromCart("user123", "PROD-12345");
        
        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    
    @Test
    void testClearCart_Success() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        assertDoesNotThrow(() -> cartService.clearCart("user123"));
        
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}