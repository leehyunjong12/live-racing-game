package com.example.racing_game.domain;

import com.example.racing_game.dto.RuleResult;
import java.util.List;
import java.util.Random;

public class GameRuleEngine {

    private final Random random;
    public static final int TRACK_LENGTH = MapDataStorage.TRACK_LENGTH;

    public GameRuleEngine(Random random) {
        this.random = random;
    }

    public RuleResult getNextPosition(int currentPosition, boolean shouldMove) {
        if (currentPosition == TRACK_LENGTH) {
            return new RuleResult(TRACK_LENGTH, 0);
        }
        if (!shouldMove) {
            return new RuleResult(currentPosition, 0);
        }

        int nextNode = findNextNode(currentPosition);

        return handleSpecialTile(nextNode);
    }

    private RuleResult handleSpecialTile(int position) {
        TileType tileType = MapDataStorage.SPECIAL_TILES.getOrDefault(position, TileType.NORMAL);
        switch (tileType) {
            case OBSTACLE:
                return new RuleResult(position, 1);
            case MOVE_BACK_NODE:
                // 30% 확률
                if (random.nextInt(100) < 30) {
                    int penaltyPos = Math.max(0, position - 2);
                    return new RuleResult(penaltyPos, 0);
                } else {
                    return new RuleResult(position, 0);
                }

            default:
                return new RuleResult(position, 0);
        }
    }


    private int findNextNode(int currentPosition) {
        if (!MapDataStorage.ADJACENCY_LIST.containsKey(currentPosition)) {
            return currentPosition;
        }

        List<Integer> choices = MapDataStorage.ADJACENCY_LIST.get(currentPosition);

        return choices.get(random.nextInt(choices.size()));
    }

}