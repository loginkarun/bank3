package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private String productId;
    private String name;
    private Double price;
    private Integer availableStock;
}