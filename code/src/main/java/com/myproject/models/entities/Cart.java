package com.myproject.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a shopping cart
 */
@Entity
@Table(name = "cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "user_id", unique = true)
    private String userId;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "total_items")
    private Integer totalItems;
    
    /**
     * Helper method to add item to cart
     */
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }
    
    /**
     * Helper method to remove item from cart
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
    
    /**
     * Calculate total amount and items
     */
    public void calculateTotals() {
        this.totalAmount = items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalItems = items.stream()
            .map(CartItem::getQuantity)
            .reduce(0, Integer::sum);
    }
}
