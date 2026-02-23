package com.critmon.pulsecheck.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class MonitorCreateRequest {
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    
    @NotNull(message = "Timeout is required")
    @Positive(message = "Timeout must be positive")
    private Integer timeout;
    
    @NotBlank(message = "Alert email is required")
    @Email(message = "Invalid email format")
    private String alertEmail;
}
