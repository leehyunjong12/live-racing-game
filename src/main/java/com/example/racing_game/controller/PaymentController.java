package com.example.racing_game.controller;

import com.example.racing_game.dto.ChargeRequest;
import com.example.racing_game.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final UserService userService;

    @PostMapping("/charge")
    public ResponseEntity<?> charge(@RequestBody ChargeRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            long newBalance = userService.chargeBalance(userDetails.getUsername(), request.getAmount());
            return ResponseEntity.ok(Map.of("message", "충전 성공", "balance", newBalance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        long balance = userService.getBalance(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("balance", balance));
    }
}