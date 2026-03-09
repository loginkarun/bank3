package com.myproject.services;

import com.myproject.models.dtos.AddToCartRequest;
import com.myproject.models.dtos.CartDTO;
import com.myproject.models.dtos.UpdateCartRequest;

public interface ICartService {
    CartDTO addItemToCart(String userId, AddToCartRequest request);
    CartDTO getCart(String userId);
    CartDTO updateCartItem(String userId, UpdateCartRequest request);
    CartDTO removeCartItem(String userId, String itemId);
    void clearCart(String userId);
}
