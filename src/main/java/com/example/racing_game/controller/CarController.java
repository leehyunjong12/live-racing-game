package com.example.racing_game.controller;

import com.example.racing_game.dto.CarRegistrationRequest;
import com.example.racing_game.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCars(@RequestBody CarRegistrationRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            carService.registerCars(userDetails.getUsername(), request.getQuantity());
            return ResponseEntity.ok(Map.of("message", "자동차 등록 완료!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}