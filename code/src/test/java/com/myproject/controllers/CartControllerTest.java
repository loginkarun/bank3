package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.ICartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private ICartService cartService;

    @InjectMocks
    private CartControllerUpdated cartController;

    private CartDTO mockCartDTO;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        List<CartItemDTO> items = new ArrayList<>();
        CartItemDTO item = CartItemDTO.builder()
                .id("item1")
                .productId("prod1")
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .quantity(1)
                .subtotal(new BigDecimal("999.99"))
                .build();
        items.add(item);

        mockCartDTO = CartDTO.builder()
                .id("cart1")
                .userId("user1")
                .items(items)
                .totalAmount(new BigDecimal("999.99"))
                .totalItems(1)
                .build();

        addToCartRequest = AddToCartRequest.builder()
                .productId("prod1")
                .quantity(1)
                .build();
    }

    @Test
    void testAddItemToCart() {
        when(cartService.addItemToCart(anyString(), any(AddToCartRequest.class)))
                .thenReturn(mockCartDTO);

        ResponseEntity<ApiResponse<CartDTO>> response = cartController.addItemToCart("user1", addToCartRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Item added to cart successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(cartService, times(1)).addItemToCart(anyString(), any(AddToCartRequest.class));
    }

    @Test
    void testGetCart() {
        when(cartService.getCart(anyString())).thenReturn(mockCartDTO);

        ResponseEntity<ApiResponse<CartDTO>> response = cartController.getCart("user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cart retrieved successfully", response.getBody().getMessage());
        verify(cartService, times(1)).getCart(anyString());
    }

    @Test
    void testUpdateCartItem() {
        UpdateCartRequest updateRequest = UpdateCartRequest.builder()
                .productId("prod1")
                .quantity(2)
                .build();

        when(cartService.updateCartItem(anyString(), any(UpdateCartRequest.class)))
                .thenReturn(mockCartDTO);

        ResponseEntity<ApiResponse<CartDTO>> response = cartController.updateCartItem("user1", updateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(cartService, times(1)).updateCartItem(anyString(), any(UpdateCartRequest.class));
    }

    @Test
    void testRemoveCartItem() {
        when(cartService.removeCartItem(anyString(), anyString())).thenReturn(mockCartDTO);

        ResponseEntity<ApiResponse<CartDTO>> response = cartController.removeCartItem("user1", "item1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(cartService, times(1)).removeCartItem(anyString(), anyString());
    }

    @Test
    void testClearCart() {
        doNothing().when(cartService).clearCart(anyString());

        ResponseEntity<ApiResponse<String>> response = cartController.clearCart("user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(cartService, times(1)).clearCart(anyString());
    }
}
