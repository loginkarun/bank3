package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private String traceId;
    private String errorCode;
    private String message;
    private List<ErrorDetail> details = new ArrayList<>();
    
    public ErrorResponse(String errorCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.message = message;
        this.traceId = java.util.UUID.randomUUID().toString();
    }
    
    public ErrorResponse(String errorCode, String message, List<ErrorDetail> details) {
        this(errorCode, message);
        this.details = details;
    }
}