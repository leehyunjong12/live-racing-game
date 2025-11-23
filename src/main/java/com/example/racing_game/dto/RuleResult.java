package com.example.racing_game.dto;


import com.example.racing_game.domain.TileType;

public record RuleResult(
        int nextPosition,
        int penaltyTurns,
        TileType triggeredEvent
) {
    public RuleResult(int nextPosition, int penaltyTurns) {
        this(nextPosition, penaltyTurns, TileType.NORMAL);
    }
}