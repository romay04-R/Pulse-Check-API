package com.critmon.pulsecheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private long activeDevices;
    private long downDevices;
    private long alertsToday;
    private double averageUptime;

}
