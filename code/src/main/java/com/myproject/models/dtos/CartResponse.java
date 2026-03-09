package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    
    private String id;
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private Double totals;
    
    public void calculateTotals() {
        this.totals = items.stream()
            .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
            .sum();
    }
}