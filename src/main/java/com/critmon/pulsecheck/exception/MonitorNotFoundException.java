package com.critmon.pulsecheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MonitorNotFoundException extends RuntimeException {
    
    public MonitorNotFoundException(String message) {
        super(message);
    }
    
    public MonitorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
