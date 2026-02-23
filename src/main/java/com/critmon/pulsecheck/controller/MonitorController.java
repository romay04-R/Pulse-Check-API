package com.critmon.pulsecheck.controller;

import com.critmon.pulsecheck.dto.ApiResponse;
import com.critmon.pulsecheck.dto.BatchMonitorCreateRequest;
import com.critmon.pulsecheck.dto.BatchMonitorResponse;
import com.critmon.pulsecheck.dto.DashboardResponse;
import com.critmon.pulsecheck.dto.MonitorCreateRequest;
import com.critmon.pulsecheck.dto.MonitorResponse;
import com.critmon.pulsecheck.model.Monitor;
import com.critmon.pulsecheck.service.MonitorServiceInterface;
import com.critmon.pulsecheck.mapper.MonitorMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitors")
public class MonitorController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);
    private final MonitorServiceInterface monitorService;

    @PostMapping
    public ResponseEntity<ApiResponse<MonitorResponse>> createMonitor(@Valid @RequestBody MonitorCreateRequest request) {
        logger.info("Creating monitor for device: {}", request.getDeviceId());
        
        Monitor monitor = monitorService.createMonitor(request.getDeviceId(), request.getTimeout(), request.getAlertEmail());
        MonitorResponse response = MonitorMapper.toResponse(monitor);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Monitor created successfully", response));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<BatchMonitorResponse>> createMonitorsBatch(@Valid @RequestBody BatchMonitorCreateRequest request) {
        logger.info("Creating batch monitors for {} devices", request.getDevices().size());
        
        BatchMonitorResponse response = monitorService.createMonitorsBatch(request);
        HttpStatus status = response.getFailed() > 0 ? 
            (response.getSuccessful() > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.BAD_REQUEST) : 
            HttpStatus.CREATED;
        
        String message = String.format("Batch creation completed: %d successful, %d failed", 
                                      response.getSuccessful(), response.getFailed());
        
        return ResponseEntity.status(status)
                .body(ApiResponse.success(message, response));
    }

    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<ApiResponse<String>> heartbeat(@PathVariable("id") String id) {
        logger.info("Heartbeat received for monitor: {}", id);
        
        Monitor monitor = monitorService.heartbeat(id);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Heartbeat received for device: " + monitor.getDeviceId()));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<String>> pauseMonitor(@PathVariable("id") String id) {
        logger.info("Pausing monitor: {}", id);
        
        Monitor monitor = monitorService.pauseMonitor(id);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Monitor paused for device: " + monitor.getDeviceId()));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<String>> resumeMonitor(@PathVariable("id") String id) {
        logger.info("Resuming monitor: {}", id);
        
        Monitor monitor = monitorService.resumeMonitor(id);
        return ResponseEntity.ok()
                .body(ApiResponse.success("Monitor resumed for device: " + monitor.getDeviceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonitorResponse>> getMonitor(@PathVariable("id") String id) {
        Monitor monitor = monitorService.getMonitor(id)
                .orElseThrow(() -> new com.critmon.pulsecheck.exception.MonitorNotFoundException("Monitor not found: " + id));
        
        MonitorResponse response = MonitorMapper.toResponse(monitor);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MonitorResponse>>> getAllMonitors() {
        List<Monitor> monitors = monitorService.getAllMonitors();
        List<MonitorResponse> responses = monitors.stream()
                .map(MonitorMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse dashboard = monitorService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMonitor(@PathVariable("id") String id) {
        logger.info("Deleting monitor: {}", id);
        monitorService.deleteMonitor(id);
        return ResponseEntity.ok(ApiResponse.success("Monitor deleted successfully"));
    }
}
