package com.myproject.services;

import com.myproject.exceptions.*;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.*;
import com.myproject.models.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private IProductService productService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart mockCart;
    private Product mockProduct;
    private ProductDTO mockProductDTO;

    @BeforeEach
    void setUp() {
        mockCart = Cart.builder()
                .id("cart1")
                .userId("user1")
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .totalItems(0)
                .build();

        mockProduct = Product.builder()
                .id("prod1")
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .availableStock(10)
                .description("High performance laptop")
                .build();

        mockProductDTO = ProductDTO.builder()
                .id("prod1")
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .availableStock(10)
                .description("High performance laptop")
                .build();
    }

    @Test
    void testAddItemToCart_Success() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId("prod1")
                .quantity(1)
                .build();

        when(productService.productExists(anyString())).thenReturn(true);
        when(productService.hasSufficientStock(anyString(), anyInt())).thenReturn(true);
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(mockCart));
        when(productService.getProductById(anyString())).thenReturn(mockProductDTO);
        when(cartItemRepository.findByCart_IdAndProductId(anyString(), anyString())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        CartDTO result = cartService.addItemToCart("user1", request);

        assertNotNull(result);
        verify(productService).productExists("prod1");
        verify(productService).hasSufficientStock("prod1", 1);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_ProductNotFound() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId("prod1")
                .quantity(1)
                .build();

        when(productService.productExists(anyString())).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            cartService.addItemToCart("user1", request);
        });
    }

    @Test
    void testAddItemToCart_InsufficientStock() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId("prod1")
                .quantity(100)
                .build();

        when(productService.productExists(anyString())).thenReturn(true);
        when(productService.hasSufficientStock(anyString(), anyInt())).thenReturn(false);

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addItemToCart("user1", request);
        });
    }

    @Test
    void testGetCart_Success() {
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(mockCart));

        CartDTO result = cartService.getCart("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
    }

    @Test
    void testGetCart_NotFound() {
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> {
            cartService.getCart("user1");
        });
    }

    @Test
    void testClearCart() {
        doNothing().when(cartRepository).deleteByUserId(anyString());

        cartService.clearCart("user1");

        verify(cartRepository, times(1)).deleteByUserId("user1");
    }
}
