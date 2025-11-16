package com.example.racing_game.service;

import com.example.racing_game.domain.Car;
import com.example.racing_game.domain.GameRuleEngine; 
import com.example.racing_game.domain.MoveStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.example.racing_game.dto.RuleResult;

public class RaceService {

    private final ObjectMapper objectMapper;
    private final MoveStrategy moveStrategy;
    private final GameRuleEngine gameRuleEngine;

    public RaceService(ObjectMapper objectMapper, MoveStrategy moveStrategy, GameRuleEngine gameRuleEngine) {
        this.objectMapper = objectMapper;
        this.moveStrategy = moveStrategy;
        this.gameRuleEngine = gameRuleEngine;
    }

    @Async
    public void startAsyncRace(List<String> carNames, int rounds, Consumer<String> broadcaster) {
        List<Car> cars = carNames.stream().map(Car::new).toList();

        try { // 0라운드
            String roundZeroJson = createJsonState(0, cars);
            broadcaster.accept(roundZeroJson);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < rounds; i++) {
            cars.forEach(car -> {
                if (car.isSkippingTurn()) {
                    car.consumeSkipTurn();
                    return;
                }
                boolean canMove = moveStrategy.shouldMove();
                RuleResult result = gameRuleEngine.getNextPosition(car.getPosition(), canMove);
                car.setPosition(result.nextPosition());
                car.setTurnsToSkip(result.penaltyTurns());
            });

            String roundResultJson = createJsonState(i + 1, cars);
            broadcaster.accept(roundResultJson);

            try { Thread.sleep(1000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            if (isRaceFinished(cars)) {
                break;
            }
        }
        String finalResultJson = createWinnerJson(cars);
        broadcaster.accept(finalResultJson);
    }

    private boolean isRaceFinished(List<Car> cars) {
        return cars.stream().anyMatch(car -> car.getPosition() == GameRuleEngine.TRACK_LENGTH);
    }

    private String createJsonState(int round, List<Car> cars) {
        List<Map<String, Object>> carStates = cars.stream()
                .map(car -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", car.getName());
                    map.put("position", car.getPosition());
                    map.put("turnsToSkip", car.getTurnsToSkip());
                    return map;})
                .toList();
        Map<String, Object> jsonMap = Map.of(
                "type", "RACING", "round", round, "cars", carStates
        );
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"ERROR\", \"message\":\"JSON 생성 실패\"}";
        }
    }
    private String createWinnerJson(List<Car> cars) {
        int maxPosition = cars.stream().mapToInt(Car::getPosition).max().orElse(0);
        List<String> winners;
        if (maxPosition < GameRuleEngine.TRACK_LENGTH) {
            winners = List.of();
        } else {
            winners = cars.stream()
                    .filter(car -> car.getPosition() == maxPosition)
                    .map(Car::getName).toList();
        }
        Map<String, Object> jsonMap = Map.of("type", "WINNER", "winners", winners);
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"ERROR\", \"message\":\"JSON 생성 실패\"}";
        }
    }
}