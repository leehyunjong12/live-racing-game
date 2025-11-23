package com.example.racing_game.controller;

import com.example.racing_game.domain.PrizePool;
import com.example.racing_game.repository.PrizePoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/prize")
@RequiredArgsConstructor
public class PrizeController {

    private final PrizePoolRepository prizePoolRepository;

    @GetMapping("/pot")
    public ResponseEntity<?> getCurrentPot() {
        PrizePool pot = prizePoolRepository.findById(1L)
                .orElseGet(PrizePool::new);

        return ResponseEntity.ok(Map.of("amount", pot.getAmount()));
    }
}