package com.example.racing_game.domain;

import com.example.racing_game.dto.MapLayoutDto;
import com.example.racing_game.dto.MapNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrackLayout {

    public MapLayoutDto getMapLayout() {
        Map<Integer, MapNode> nodeMap = new HashMap<>();

        for (Integer nodeId : MapDataStorage.LAYOUT_COORDS.keySet()) {
            nodeMap.put(nodeId, new MapNode(
                    nodeId,
                    MapDataStorage.LAYOUT_COORDS.get(nodeId)[0],
                    MapDataStorage.LAYOUT_COORDS.get(nodeId)[1],
                    MapDataStorage.SPECIAL_TILES.getOrDefault(nodeId, TileType.NORMAL)
            ));
        }
        return MapLayoutDto.of(Collections.unmodifiableMap(nodeMap), MapDataStorage.LAYOUT_LINES);
    }
}