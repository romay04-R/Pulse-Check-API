package com.critmon.pulsecheck.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;



@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "monitors")
public class Monitor {
    
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String deviceId;

    private int timeout;
    

    private String alertEmail;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    

    @Builder.Default
    private LocalDateTime lastHeartbeat = LocalDateTime.now();
    

    @Builder.Default
    private boolean isActive = true;
    

    @Builder.Default
    private boolean isPaused = false;
    

    private LocalDateTime expiresAt;

    public void resetTimer() {
        this.lastHeartbeat = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusSeconds(timeout);
        this.isPaused = false;
        this.isActive = true;
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
        this.resetTimer();
    }

    public boolean isExpired() {
        return isActive && !isPaused && expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }


}
