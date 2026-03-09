package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    private String productId;
    private String name;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    
    public void calculateSubtotal() {
        if (price != null && quantity != null) {
            this.subtotal = price * quantity;
        }
    }
}