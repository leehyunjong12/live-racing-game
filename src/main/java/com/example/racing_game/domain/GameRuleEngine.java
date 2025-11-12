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
        if (MapDataStorage.JUNCTIONS.containsKey(currentPosition)) {
            return handleJunction(currentPosition);
        }
        return handleNormalMove(currentPosition);
    }

    private int handleSpecialTile(int currentPosition) {
        TileType tileType = MapDataStorage.SPECIAL_TILES.get(currentPosition);
        return switch (tileType) {
            case MOVE_BACK -> Math.max(0, currentPosition - 2);
            case SHORTCUT -> MapDataStorage.SHORTCUT_DESTINATIONS.get(currentPosition);
            default -> currentPosition;
        };
    }

    private int handleJunction(int currentPosition) {
        List<Integer> choices = MapDataStorage.JUNCTIONS.get(currentPosition);
        int choice = choices.get(random.nextInt(choices.size()));
        return MapDataStorage.SHORTCUT_DESTINATIONS.getOrDefault(choice, choice);
    }

    private int handleNormalMove(int currentPosition) {
        if (MapDataStorage.PRE_FINISH_NODES.contains(currentPosition)) {
            return TRACK_LENGTH;
        }
        return Math.min(TRACK_LENGTH, currentPosition + 1);
    }
}