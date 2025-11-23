package com.example.racing_game.domain;

import com.example.racing_game.dto.RuleResult;
import java.util.List;
import java.util.Random;

public class GameRuleEngine {
    private static final int NO_PENALTY = 0;
    private final Random random;
    public static final int TRACK_LENGTH = MapDataStorage.TRACK_LENGTH;

    public GameRuleEngine(Random random) {
        this.random = random;
    }

    public RuleResult getNextPosition(int currentPosition, boolean shouldMove) {
        if (currentPosition == TRACK_LENGTH) {
            return new RuleResult(TRACK_LENGTH, NO_PENALTY);
        }
        if (!shouldMove) {
            return new RuleResult(currentPosition, NO_PENALTY);
        }

        int nextNode = findNextNode(currentPosition);

        return handleSpecialTile(nextNode);
    }

    private RuleResult handleSpecialTile(int position) {
        TileType tileType = MapDataStorage.SPECIAL_TILES.getOrDefault(position, TileType.NORMAL);
        switch (tileType) {
            case JAIL:
                // 30% 확률
                if (random.nextInt(100) < 30) {
                    return new RuleResult(position, 2, TileType.JAIL);
                } else {
                    return new RuleResult(position, NO_PENALTY, TileType.NORMAL);
                }

            case MOVE_BACK_NODE:
                // 30% 확률
                if (random.nextInt(100) < 30) {
                    int penaltyPos = Math.max(0, position - 2);
                    return new RuleResult(penaltyPos, NO_PENALTY, TileType.MOVE_BACK_NODE);
                } else {
                    return new RuleResult(position, NO_PENALTY, TileType.NORMAL);
                }
            case MOVE_TO_MIDPOINTS:
                int randomPoint = random.nextInt(10, 21);
                return new RuleResult(randomPoint,NO_PENALTY, TileType.MOVE_TO_MIDPOINTS);
            case MOVE_TO_START:
                return new RuleResult(0, NO_PENALTY,TileType.MOVE_TO_START);
            default:
                return new RuleResult(position, NO_PENALTY);
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