package com.example.racing_game.service;

import com.example.racing_game.domain.Car;
import com.example.racing_game.domain.GameRuleEngine;
import com.example.racing_game.domain.MoveStrategy;
import com.example.racing_game.domain.TileType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
    private final PrizeService prizeService;

    public RaceService(ObjectMapper objectMapper, MoveStrategy moveStrategy, GameRuleEngine gameRuleEngine,
                       PrizeService prizeService) {
        this.objectMapper = objectMapper;
        this.moveStrategy = moveStrategy;
        this.gameRuleEngine = gameRuleEngine;
        this.prizeService = prizeService;
    }

    @Async
    public void startAsyncRace(List<String> carNames, int rounds, Consumer<String> broadcaster) {
        List<Car> cars = carNames.stream().map(Car::new).toList();

        try { // 0라운드
            String roundZeroJson = createJsonState(0, rounds, cars, new ArrayList<>());
            broadcaster.accept(roundZeroJson);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < rounds; i++) {
            List<String> turnEvents = new ArrayList<>();
            cars.forEach(car -> {
                if (car.isSkippingTurn()) {
                    car.consumeSkipTurn();
                    return;
                }
                boolean canMove = moveStrategy.shouldMove();
                RuleResult result = gameRuleEngine.getNextPosition(car.getPosition(), canMove);
                car.setPosition(result.nextPosition());
                car.setTurnsToSkip(result.penaltyTurns());
                if (result.triggeredEvent() != TileType.NORMAL) {
                    String eventCode = result.triggeredEvent().name() + ":" + car.getName();
                    turnEvents.add(eventCode);
                }
            });

            String roundResultJson = createJsonState(i + 1, rounds, cars, turnEvents);
            broadcaster.accept(roundResultJson);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (isRaceFinished(cars)) {
                break;
            }
        }
        List<String> winners = calculateWinners(cars);
        prizeService.awardWinnersAndReset(winners);
        String finalResultJson = createWinnerJson(winners);
        broadcaster.accept(finalResultJson);

    }

    private boolean isRaceFinished(List<Car> cars) {
        return cars.stream().anyMatch(car -> car.getPosition() == GameRuleEngine.TRACK_LENGTH);
    }

    private String createJsonState(int round, int totalRounds, List<Car> cars, List<String> events) {
        List<Map<String, Object>> carStates = cars.stream()
                .map(car -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", car.getName());
                    map.put("position", car.getPosition());
                    map.put("turnsToSkip", car.getTurnsToSkip());
                    return map;
                })
                .toList();
        Map<String, Object> jsonMap = Map.of(
                "type", "RACING", "round", round, "totalRounds", totalRounds, "cars", carStates, "events", events
        );
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"ERROR\", \"message\":\"JSON 생성 실패\"}";
        }
    }

    private List<String> calculateWinners(List<Car> cars) {
        int maxPosition = cars.stream().mapToInt(Car::getPosition).max().orElse(0);

        if (maxPosition < GameRuleEngine.TRACK_LENGTH) {
            return List.of();
        }

        return cars.stream()
                .filter(car -> car.getPosition() == maxPosition)
                .map(Car::getName)
                .toList();
    }

    private String createWinnerJson(List<String> winners) {
        Map<String, Object> jsonMap = Map.of("type", "WINNER", "winners", winners);
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"ERROR\", \"message\":\"JSON 생성 실패\"}";
        }
    }
}