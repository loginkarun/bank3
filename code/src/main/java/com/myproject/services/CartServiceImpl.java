package com.myproject.services;

import com.myproject.exceptions.*;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.*;
import com.myproject.models.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final IProductService productService;

    @Override
    @Transactional
    public CartDTO addItemToCart(String userId, AddToCartRequest request) {
        if (!productService.productExists(request.getProductId())) {
            throw new ProductNotFoundException("Product not found: " + request.getProductId());
        }
        if (!productService.hasSufficientStock(request.getProductId(), request.getQuantity())) {
            throw new InsufficientStockException("Insufficient stock for product: " + request.getProductId());
        }
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        var productDTO = productService.getProductById(request.getProductId());
        CartItem existingItem = cartItemRepository
                .findByCart_IdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.calculateSubtotal();
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .name(productDTO.getName())
                    .price(productDTO.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            newItem.calculateSubtotal();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }
        cart.calculateTotals();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String userId, UpdateCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        if (!productService.hasSufficientStock(request.getProductId(), request.getQuantity())) {
            throw new InsufficientStockException("Insufficient stock for product: " + request.getProductId());
        }
        CartItem item = cartItemRepository
                .findByCart_IdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Item not found in cart: " + request.getProductId()));
        item.setQuantity(request.getQuantity());
        item.calculateSubtotal();
        cartItemRepository.save(item);
        cart.calculateTotals();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(String userId, String itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ProductNotFoundException("Item not found: " + itemId));
        cart.removeItem(item);
        cartItemRepository.delete(item);
        cart.calculateTotals();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemDTOs)
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .build();
    }
}
