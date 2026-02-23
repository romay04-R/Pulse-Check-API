package com.critmon.pulsecheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMonitorResponse {
    
    private int totalRequested;
    private int successful;
    private int failed;
    private List<MonitorResponse> createdMonitors;
    private List<BatchError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchError {
        private String deviceId;
        private String error;
    }
}
