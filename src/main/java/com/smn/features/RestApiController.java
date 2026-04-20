package com.smn.features;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RestApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
    
    // Sample data for demonstration
    private final Map<Long, User> users = new HashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("RestApiController has been created and initialized by Spring!");
        // Initialize sample data
        users.put(1L, new User(1L, "John Doe", "john.doe@example.com", 30));
        users.put(2L, new User(2L, "Jane Smith", "jane.smith@example.com", 25));
        users.put(3L, new User(3L, "Bob Johnson", "bob.johnson@example.com", 35));
    }
    
    // Simple GET endpoint
    @GetMapping("/test")
    public String testEndpoint() {
        logger.info("Test endpoint called!");
        return "Hello, World! This is a test GET endpoint.";
    }
    
    // GET endpoint returning JSON object
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        logger.info("Status endpoint called!");
        Map<String, Object> status = new HashMap<>();
        status.put("service", "Spring Boot REST API");
        status.put("status", "UP");
        status.put("timestamp", new Date());
        status.put("version", "1.0.0");
        return status;
    }
    
    // GET endpoint with path variable
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        logger.info("Getting user with ID: {}", userId);
        User user = users.get(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET endpoint returning list of objects
    @GetMapping("/users")
    public List<User> getAllUsers() {
        logger.info("Getting all users");
        return new ArrayList<>(users.values());
    }
    
    // GET endpoint with query parameters
    @GetMapping("/users/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        
        logger.info("Searching users with name: {}, minAge: {}, maxAge: {}", name, minAge, maxAge);
        
        return users.values().stream()
                .filter(user -> name == null || user.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(user -> minAge == null || user.getAge() >= minAge)
                .filter(user -> maxAge == null || user.getAge() <= maxAge)
                .toList();
    }
    
    // GET endpoint with multiple path variables
    @GetMapping("/users/{userId}/profile/{section}")
    public ResponseEntity<Map<String, Object>> getUserProfileSection(
            @PathVariable Long userId, 
            @PathVariable String section) {
        
        logger.info("Getting profile section '{}' for user ID: {}", section, userId);
        User user = users.get(userId);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("section", section);
        
        switch (section.toLowerCase()) {
            case "basic":
                response.put("data", Map.of("name", user.getName(), "id", user.getId()));
                break;
            case "contact":
                response.put("data", Map.of("email", user.getEmail()));
                break;
            case "demographics":
                response.put("data", Map.of("age", user.getAge()));
                break;
            default:
                response.put("error", "Unknown profile section: " + section);
                return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Simple data model class
    public static class User {
        private Long id;
        private String name;
        private String email;
        private int age;
        
        public User() {}
        
        public User(Long id, String name, String email, int age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}