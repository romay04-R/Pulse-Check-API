package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.dto.BatchMonitorCreateRequest;
import com.critmon.pulsecheck.dto.BatchMonitorResponse;
import com.critmon.pulsecheck.dto.DashboardResponse;
import com.critmon.pulsecheck.model.Monitor;

import java.util.List;
import java.util.Optional;

public interface MonitorServiceInterface {
    
    Monitor createMonitor(String deviceId, int timeout, String alertEmail);
    
    BatchMonitorResponse createMonitorsBatch(BatchMonitorCreateRequest request);
    
    Optional<Monitor> getMonitor(String id);
    
    Monitor heartbeat(String id);
    
    Monitor pauseMonitor(String id);
    
    Monitor resumeMonitor(String id);
    
    List<Monitor> getAllMonitors();
    
    DashboardResponse getDashboardStats();
    
    void deleteMonitor(String id);
}
