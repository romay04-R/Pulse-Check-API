package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.dto.BatchMonitorCreateRequest;
import com.critmon.pulsecheck.dto.BatchMonitorResponse;
import com.critmon.pulsecheck.dto.DashboardResponse;
import com.critmon.pulsecheck.dto.MonitorResponse;
import com.critmon.pulsecheck.exception.MonitorNotFoundException;
import com.critmon.pulsecheck.mapper.MonitorMapper;
import com.critmon.pulsecheck.model.Monitor;
import com.critmon.pulsecheck.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    
    private final MonitorRepository monitorRepository;
    private ScheduledExecutorService scheduler;
    
    @PostConstruct
    public void init() {
        this.scheduler = Executors.newScheduledThreadPool(10);
    }
    
    @Override
    @CachePut(value = "monitors", key = "#result.id")
    public Monitor createMonitor(String deviceId, int timeout, String alertEmail) {
        logger.info("Creating monitor for device: {} with timeout: {}s", deviceId, timeout);
        
        Monitor monitor = Monitor.builder()
                .deviceId(deviceId)
                .timeout(timeout)
                .alertEmail(alertEmail)
                .build();
        monitor = monitorRepository.save(monitor);
        
        // Initialize timer for new monitor
        monitor.resetTimer();
        monitorRepository.save(monitor);
        return monitor;
    }
    
    @Override
    @CacheEvict(value = "allMonitors", allEntries = true)
    public BatchMonitorResponse createMonitorsBatch(BatchMonitorCreateRequest request) {
        logger.info("Creating batch monitors for {} devices", request.getDevices().size());
        
        List<MonitorResponse> createdMonitors = new ArrayList<>();
        List<BatchMonitorResponse.BatchError> errors = new ArrayList<>();
        
        for (BatchMonitorCreateRequest.DeviceRequest deviceRequest : request.getDevices()) {
            try {
                // Check if monitor already exists for this device
                List<Monitor> existingMonitors = monitorRepository.findByDeviceId(deviceRequest.getId());
                if (!existingMonitors.isEmpty()) {
                    errors.add(BatchMonitorResponse.BatchError.builder()
                            .deviceId(deviceRequest.getId())
                            .error("Monitor already exists for device: " + deviceRequest.getId())
                            .build());
                    continue;
                }
                
                Monitor monitor = createMonitor(
                    deviceRequest.getId(),
                    deviceRequest.getTimeout(),
                    deviceRequest.getAlert_email()
                );
                
                createdMonitors.add(MonitorMapper.toResponse(monitor));
                logger.debug("Successfully created monitor for device: {}", deviceRequest.getId());
                
            } catch (Exception e) {
                logger.error("Failed to create monitor for device: {}", deviceRequest.getId(), e);
                errors.add(BatchMonitorResponse.BatchError.builder()
                        .deviceId(deviceRequest.getId())
                        .error("Failed to create monitor: " + e.getMessage())
                        .build());
            }
        }
        
        BatchMonitorResponse response = BatchMonitorResponse.builder()
                .totalRequested(request.getDevices().size())
                .successful(createdMonitors.size())
                .failed(errors.size())
                .createdMonitors(createdMonitors)
                .errors(errors)
                .build();
        
        logger.info("Batch creation completed: {} successful, {} failed", 
                   response.getSuccessful(), response.getFailed());
        
        return response;
    }
    
    @Override
    @Cacheable(value = "monitors", key = "#p0")
    public Optional<Monitor> getMonitor(String id) {
        logger.debug("Fetching monitor from database: {}", id);
        return monitorRepository.findById(id);
    }
    
    @Override
    @CachePut(value = "monitors", key = "#p0")
    public Monitor heartbeat(String id) {
        logger.debug("Heartbeat received for monitor: {}", id);
        
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + id));
        
        logger.debug("Before reset - Last heartbeat: {}, Expires at: {}, Timeout: {}s", 
                    monitor.getLastHeartbeat(), monitor.getExpiresAt(), monitor.getTimeout());
        
        monitor.resetTimer();
        monitorRepository.save(monitor);
        
        logger.debug("After reset - Last heartbeat: {}, Expires at: {}, Timeout: {}s", 
                    monitor.getLastHeartbeat(), monitor.getExpiresAt(), monitor.getTimeout());
        
        return monitor;
    }
    
    @Override
    @CachePut(value = "monitors", key = "#p0")
    public Monitor pauseMonitor(String id) {
        logger.info("Pausing monitor: {}", id);
        
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + id));
        
        monitor.pause();
        monitorRepository.save(monitor);
        
        logger.info("Monitor paused: {}", id);
        return monitor;
    }
    
    @Override
    @CachePut(value = "monitors", key = "#p0")
    public Monitor resumeMonitor(String id) {
        logger.info("Resuming monitor: {}", id);
        
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found: " + id));
        
        monitor.resume();
        monitorRepository.save(monitor);
        
        logger.info("Monitor resumed: {}", id);
        return monitor;
    }
    
    @Override
    @Cacheable(value = "allMonitors")
    public List<Monitor> getAllMonitors() {
        logger.debug("Fetching all monitors from database");
        return monitorRepository.findAll();
    }
    
    @Override
    @Cacheable(value = "dashboardStats")
    public DashboardResponse getDashboardStats() {
        logger.debug("Calculating dashboard statistics");
        
        List<Monitor> allMonitors = monitorRepository.findAll();
        
        long activeDevices = allMonitors.stream()
                .filter(m -> m.isActive() && !m.isPaused())
                .count();
        
        long downDevices = allMonitors.stream()
                .filter(m -> m.isActive() && m.isExpired())
                .count();
        long alertsToday = downDevices;

        double averageUptime = allMonitors.isEmpty() ? 0.0 : 
            (double) activeDevices / allMonitors.size() * 100.0;
        
        return DashboardResponse.builder()
                .activeDevices(activeDevices)
                .downDevices(downDevices)
                .alertsToday(alertsToday)
                .averageUptime(Math.round(averageUptime * 10.0) / 10.0) // Round to 1 decimal place
                .build();
    }
    
    @Override
    @CacheEvict(value = {"monitors", "allMonitors"}, key = "#p0")
    public void deleteMonitor(String id) {
        if (!monitorRepository.existsById(id)) {
            throw new MonitorNotFoundException("Monitor not found: " + id);
        }
        monitorRepository.deleteById(id);
        logger.info("Monitor deleted: {}", id);
    }


}
