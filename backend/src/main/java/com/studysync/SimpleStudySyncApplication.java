package com.studysync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SimpleStudySyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleStudySyncApplication.class, args);
    }
}

@RestController
class SimpleController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🎉 StudySync API is running!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "healthy");
        response.put("version", "1.0.0-demo");
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "StudySync");
        return health;
    }
    
    @GetMapping("/demo")
    public Map<String, Object> demo() {
        Map<String, Object> demo = new HashMap<>();
        demo.put("message", "StudySync Demo API");
        demo.put("endpoints", new String[]{
            "GET / - Home",
            "GET /health - Health check",
            "GET /demo - This endpoint"
        });
        demo.put("description", "Social study tracking application");
        return demo;
    }
}


