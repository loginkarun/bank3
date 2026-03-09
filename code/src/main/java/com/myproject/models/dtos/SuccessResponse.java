package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    
    private String message;
    private LocalDateTime timestamp;
    
    public SuccessResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}