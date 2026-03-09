package com.myproject.services.interfaces;

import com.myproject.models.dtos.AddItemRequest;
import com.myproject.models.dtos.CartResponse;
import com.myproject.models.dtos.UpdateItemRequest;

public interface CartService {
    
    CartResponse addItemToCart(String userId, AddItemRequest request);
    
    CartResponse getCart(String userId);
    
    CartResponse updateCartItem(String userId, UpdateItemRequest request);
    
    CartResponse removeItemFromCart(String userId, String productId);
    
    void clearCart(String userId);
}