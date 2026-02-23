package com.critmon.pulsecheck.mapper;

import com.critmon.pulsecheck.dto.MonitorResponse;
import com.critmon.pulsecheck.model.Monitor;

public class MonitorMapper {
    
    public static MonitorResponse toResponse(Monitor monitor) {
        return MonitorResponse.builder()
                .id(monitor.getId())
                .deviceId(monitor.getDeviceId())
                .timeout(monitor.getTimeout())
                .alertEmail(monitor.getAlertEmail())
                .createdAt(monitor.getCreatedAt())
                .lastHeartbeat(monitor.getLastHeartbeat())
                .isActive(monitor.isActive())
                .isPaused(monitor.isPaused())
                .expiresAt(monitor.getExpiresAt())
                .build();
    }
    
    public static MonitorResponse toSimpleResponse(Monitor monitor) {
        return MonitorResponse.builder()
                .id(monitor.getId())
                .deviceId(monitor.getDeviceId())
                .isActive(monitor.isActive())
                .isPaused(monitor.isPaused())
                .expiresAt(monitor.getExpiresAt())
                .build();
    }
}
