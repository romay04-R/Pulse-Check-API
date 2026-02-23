package com.critmon.pulsecheck.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchMonitorCreateRequest {
    
    @Valid
    @NotNull(message = "Devices list cannot be null")
    private List<DeviceRequest> devices;
    
    @Data
    public static class DeviceRequest {
        @NotBlank(message = "Device ID is required")
        private String id;
        
        @NotNull(message = "Timeout is required")
        @Min(value = 1, message = "Timeout must be at least 1 second")
        private Integer timeout;
        
        @NotBlank(message = "Alert email is required")
        @Email(message = "Invalid email format")
        private String alert_email;
    }
}
