package com.critmon.pulsecheck.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Builder
public class MonitorResponse {
    private String id;
    private String deviceId;
    private int timeout;
    private String alertEmail;
    private LocalDateTime createdAt;
    private LocalDateTime lastHeartbeat;
    private boolean isActive;
    private boolean isPaused;
    private LocalDateTime expiresAt;
}
