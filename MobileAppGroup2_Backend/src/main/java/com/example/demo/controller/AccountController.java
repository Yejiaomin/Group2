package com.example.demo.controller;

import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public AccountController(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/exists")
    public ResponseEntity<?> checkAccountExists(@RequestParam String email) {
        boolean isUser = userRepository.findByEmail(email) != null;
        boolean isRestaurant = restaurantRepository.findByEmail(email) != null;

        if (isUser) {
            return ResponseEntity.ok(Map.of("exists", true, "userType", "user"));
        } else if (isRestaurant) {
            return ResponseEntity.ok(Map.of("exists", true, "userType", "restaurant"));
        } else {
            return ResponseEntity.ok(Map.of("exists", false));
        }
    }
}