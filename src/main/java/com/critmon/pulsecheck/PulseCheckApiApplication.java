package com.critmon.pulsecheck;

import com.critmon.pulsecheck.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class PulseCheckApiApplication {
    public static void main(String[] args) {
        DotenvConfig.loadEnvironmentVariables();
        
        SpringApplication.run(PulseCheckApiApplication.class, args);
    }
}
