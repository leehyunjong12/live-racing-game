package com.example.racing_game.domain;

import java.util.List;
import java.util.Map;

/**
 * 맵의 "원본 데이터"를 보관하는 유일한 클래스 (불변)
 */
public final class MapDataStorage {


    public static final int TRACK_LENGTH = 20;

    public static final Map<Integer, TileType> SPECIAL_TILES = Map.of(
            4, TileType.SHORTCUT,
            8, TileType.OBSTACLE,
            12, TileType.MOVE_BACK
    );

    public static final Map<Integer, List<Integer>> JUNCTIONS = Map.of(
            2, List.of(3, 10),
            13, List.of(14, 18)
    );

    public static final Map<Integer, Integer> SHORTCUT_DESTINATIONS = Map.of(
            4, 9,
            10, 15,
            18, 19
    );

    public static final List<Integer> PRE_FINISH_NODES = List.of(9, 17, 19);

    public static final Map<Integer, int[]> LAYOUT_COORDS = Map.ofEntries(
            Map.entry(0, new int[]{30, 250}),
            Map.entry(1, new int[]{80, 250}),
            Map.entry(2, new int[]{130, 250}),
            Map.entry(3, new int[]{180, 250}),
            Map.entry(4, new int[]{230, 250}),
            Map.entry(5, new int[]{280, 250}),
            Map.entry(6, new int[]{330, 250}),
            Map.entry(7, new int[]{380, 250}),
            Map.entry(8, new int[]{430, 250}),
            Map.entry(9, new int[]{480, 250}),
            Map.entry(10, new int[]{180, 150}),
            Map.entry(11, new int[]{230, 150}),
            Map.entry(12, new int[]{280, 150}),
            Map.entry(13, new int[]{330, 150}),
            Map.entry(14, new int[]{380, 150}),
            Map.entry(15, new int[]{430, 150}),
            Map.entry(16, new int[]{480, 150}),
            Map.entry(17, new int[]{530, 150}),
            Map.entry(18, new int[]{380, 50}),
            Map.entry(19, new int[]{430, 50}),
            Map.entry(20, new int[]{950, 250})
    );

    public static final List<List<Integer>> LAYOUT_LINES = List.of(
            List.of(0, 1), List.of(1, 2), List.of(2, 3), List.of(3, 4), List.of(4, 5),
            List.of(5, 6), List.of(6, 7), List.of(7, 8), List.of(8, 9), List.of(9, 20),
            List.of(2, 10), List.of(10, 11), List.of(11, 12), List.of(12, 13), List.of(13, 14),
            List.of(14, 15), List.of(15, 16), List.of(16, 17), List.of(17, 20),
            List.of(13, 18), List.of(18, 19), List.of(19, 20),
            List.of(4, 9), List.of(10, 15), List.of(18, 19)
    );


}