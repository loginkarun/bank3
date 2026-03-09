package com.myproject.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItemEntity> items = new ArrayList<>();
    
    @Column(name = "totals", nullable = false)
    private Double totals = 0.0;
    
    public void calculateTotals() {
        this.totals = items.stream()
            .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
            .sum();
    }
    
    public void addItem(CartItemEntity item) {
        items.add(item);
        item.setCart(this);
        calculateTotals();
    }
    
    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.setCart(null);
        calculateTotals();
    }
}