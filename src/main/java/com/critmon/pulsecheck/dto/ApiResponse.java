package com.critmon.pulsecheck.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    /**
     * Creates a success response.
     * 
     * @param data the response data
     * @return success ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a success response with custom message.
     * 
     * @param message the success message
     * @param data the response data
     * @return success ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response.
     * 
     * @param message the error message
     * @return error ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
