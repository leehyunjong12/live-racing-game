package com.example.racing_game.dto;

import java.util.List;
import java.util.Map;

public record MapLayoutDto(
        String type,
        Map<Integer, MapNode> nodes,
        List<List<Integer>> lines
) {
    public static MapLayoutDto of(Map<Integer, MapNode> nodes, List<List<Integer>> lines) {
        return new MapLayoutDto("MAP_LAYOUT", nodes, lines);
    }
}