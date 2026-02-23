package com.critmon.pulsecheck.repository;

import com.critmon.pulsecheck.model.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, String> {
    List<Monitor> findByDeviceId(String deviceId);
    @Query("SELECT m FROM Monitor m WHERE m.isActive = true")
    List<Monitor> findActiveMonitors();

    @Query("SELECT COUNT(m) FROM Monitor m WHERE m.isActive = true")
    long countActiveMonitors();

    List<Monitor> findByAlertEmail(String alertEmail);

    @Query("SELECT m FROM Monitor m WHERE m.isActive = true AND " +
           "m.lastHeartbeat <= CURRENT_TIMESTAMP - (:minutes * 60) SECOND")
    List<Monitor> findMonitorsExpiringWithin(@Param("minutes") int minutes);

    @Query("SELECT m FROM Monitor m WHERE m.isActive = true AND m.isPaused = false AND m.expiresAt < CURRENT_TIMESTAMP")
    List<Monitor> findExpiredMonitors();
}
