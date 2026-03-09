package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CartService cartService;
    
    private CartResponse cartResponse;
    private AddItemRequest addItemRequest;
    private UpdateItemRequest updateItemRequest;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        CartItem cartItem = new CartItem("PROD-12345", "Wireless Mouse", 29.99, 2, 59.98);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        
        cartResponse = new CartResponse();
        cartResponse.setId("CART-001");
        cartResponse.setUserId("user123");
        cartResponse.setItems(items);
        cartResponse.setTotals(59.98);
        
        addItemRequest = new AddItemRequest("PROD-12345", 2);
        updateItemRequest = new UpdateItemRequest("PROD-12345", 3);
    }
    
    @Test
    @WithMockUser(username = "user123")
    void testAddItemToCart_Success() throws Exception {
        when(cartService.addItemToCart(any(String.class), any(AddItemRequest.class)))
            .thenReturn(cartResponse);
        
        mockMvc.perform(post("/api/cart/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addItemRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("CART-001"))
            .andExpect(jsonPath("$.userId").value("user123"))
            .andExpect(jsonPath("$.totals").value(59.98));
        
        verify(cartService, times(1)).addItemToCart(any(String.class), any(AddItemRequest.class));
    }
    
    @Test
    @WithMockUser(username = "user123")
    void testGetCart_Success() throws Exception {
        when(cartService.getCart(any(String.class))).thenReturn(cartResponse);
        
        mockMvc.perform(get("/api/cart")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("CART-001"))
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items[0].productId").value("PROD-12345"));
        
        verify(cartService, times(1)).getCart(any(String.class));
    }
    
    @Test
    @WithMockUser(username = "user123")
    void testUpdateCartItem_Success() throws Exception {
        when(cartService.updateCartItem(any(String.class), any(UpdateItemRequest.class)))
            .thenReturn(cartResponse);
        
        mockMvc.perform(put("/api/cart/update")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateItemRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("CART-001"));
        
        verify(cartService, times(1)).updateCartItem(any(String.class), any(UpdateItemRequest.class));
    }
    
    @Test
    @WithMockUser(username = "user123")
    void testRemoveItemFromCart_Success() throws Exception {
        when(cartService.removeItemFromCart(any(String.class), eq("PROD-12345")))
            .thenReturn(cartResponse);
        
        mockMvc.perform(delete("/api/cart/remove/PROD-12345")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("CART-001"));
        
        verify(cartService, times(1)).removeItemFromCart(any(String.class), eq("PROD-12345"));
    }
    
    @Test
    @WithMockUser(username = "user123")
    void testClearCart_Success() throws Exception {
        doNothing().when(cartService).clearCart(any(String.class));
        
        mockMvc.perform(delete("/api/cart/clear")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Cart cleared successfully"));
        
        verify(cartService, times(1)).clearCart(any(String.class));
    }
}