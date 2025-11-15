package com.example.racing_game.dto;


/**
 * 규칙 엔진이 계산한 결과
 * @param nextPosition 다음 위치
 * @param penaltyTurns 다음 턴에 쉴 횟수 (0이면 쉼 없음)
 */
public record RuleResult(
        int nextPosition,
        int penaltyTurns
) {}