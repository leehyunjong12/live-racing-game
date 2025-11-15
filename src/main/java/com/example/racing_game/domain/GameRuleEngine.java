package com.example.racing_game.domain;

import java.util.List;
import java.util.Random;

public class GameRuleEngine {

    private final Random random = new Random();
    public static final int TRACK_LENGTH = MapDataStorage.TRACK_LENGTH;

    public int getNextPosition(int currentPosition, boolean shouldMove) {
        if (currentPosition == TRACK_LENGTH) { return TRACK_LENGTH; }
        if (!shouldMove) { return currentPosition; }

        if (MapDataStorage.SPECIAL_TILES.containsKey(currentPosition)) {
            return handleSpecialTile(currentPosition);
        }

        return findNextNode(currentPosition);
    }

    private int handleSpecialTile(int currentPosition) {
        TileType tileType = MapDataStorage.SPECIAL_TILES.get(currentPosition);
        switch (tileType) {
            case OBSTACLE:
                return currentPosition;

            case MOVE_BACK_NODE:
                // 30% 확률
                if (random.nextInt(100) < 30) {
                    return Math.max(0, currentPosition - 2);
                } else {
                    return findNextNode(currentPosition);
                }

            default:
                return currentPosition;
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