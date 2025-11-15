package com.example.racing_game.domain;

import java.util.List;
import java.util.Map;

/**
 * 맵의 "원본 데이터"를 보관하는 유일한 클래스 (불변)
 */
public final class MapDataStorage {

    public static final int TRACK_LENGTH = 30;

    public static final Map<Integer, TileType> SPECIAL_TILES = Map.of(
            23, TileType.OBSTACLE,
            28, TileType.OBSTACLE,
            6, TileType.MOVE_BACK_NODE,
            17, TileType.MOVE_BACK_NODE,
            27, TileType.MOVE_BACK_NODE
    );

    public static final Map<Integer, List<Integer>> ADJACENCY_LIST = Map.ofEntries(
            Map.entry(0, List.of(1)),
            Map.entry(1, List.of(2, 3, 9)),

            Map.entry(2, List.of(5)),
            Map.entry(5, List.of(7)),
            Map.entry(7, List.of(4, 11)),
            Map.entry(4, List.of(10)),
            Map.entry(10, List.of(7)),

            Map.entry(11, List.of(15, 3)),
            Map.entry(15, List.of(19)),
            Map.entry(19, List.of(22)),
            Map.entry(22, List.of(24)),
            Map.entry(24, List.of(27, 26)),
            Map.entry(27, List.of(28)),
            Map.entry(28, List.of(30)),

            Map.entry(3, List.of(6)),
            Map.entry(6, List.of(8)),
            Map.entry(8, List.of(12)),
            Map.entry(12, List.of(14, 16)),
            Map.entry(14, List.of(18)),
            Map.entry(18, List.of(21)),
            Map.entry(21, List.of(24)),
            Map.entry(16, List.of(19, 23)),

            Map.entry(9, List.of(13)),
            Map.entry(13, List.of(17)),
            Map.entry(17, List.of(20)),
            Map.entry(20, List.of(23)),
            Map.entry(23, List.of(25)),
            Map.entry(25, List.of(30, 29))
    );

    public static final Map<Integer, int[]> LAYOUT_COORDS = Map.ofEntries(
            Map.entry(0, new int[]{50, 250}),
            Map.entry(1, new int[]{120, 250}),

            Map.entry(2, new int[]{200, 150}),
            Map.entry(5, new int[]{280, 150}),
            Map.entry(7, new int[]{360, 150}),
            Map.entry(11, new int[]{440, 150}),
            Map.entry(15, new int[]{520, 150}),
            Map.entry(19, new int[]{600, 150}),
            Map.entry(22, new int[]{680, 150}),
            Map.entry(24, new int[]{760, 150}),
            Map.entry(27, new int[]{840, 150}),
            Map.entry(28, new int[]{920, 150}),

            Map.entry(4, new int[]{280, 50}),
            Map.entry(10, new int[]{440, 50}),

            Map.entry(3, new int[]{200, 250}),
            Map.entry(6, new int[]{280, 250}),
            Map.entry(8, new int[]{360, 250}),
            Map.entry(12, new int[]{440, 250}),
            Map.entry(16, new int[]{520, 250}),

            Map.entry(14, new int[]{520, 50}),
            Map.entry(18, new int[]{600, 50}),
            Map.entry(21, new int[]{680, 50}),
            Map.entry(26, new int[]{840, 50}),

            Map.entry(9, new int[]{360, 350}),
            Map.entry(13, new int[]{440, 350}),
            Map.entry(17, new int[]{520, 350}),
            Map.entry(20, new int[]{600, 350}),
            Map.entry(23, new int[]{680, 350}),
            Map.entry(25, new int[]{760, 350}),

            Map.entry(29, new int[]{840, 400}),
            Map.entry(30, new int[]{950, 250})
    );

    public static final List<List<Integer>> LAYOUT_LINES = List.of(
            List.of(0, 1), List.of(1, 2), List.of(1, 3), List.of(1, 9),
            List.of(2, 5), List.of(5, 7), List.of(7, 4), List.of(4, 10), List.of(10, 7), List.of(7, 11),
            List.of(11, 15), List.of(15, 19), List.of(19, 22), List.of(22, 24), List.of(24, 27), List.of(24, 26),
            List.of(27, 28),
            List.of(28, 30), List.of(3, 6), List.of(11, 3), List.of(6, 8), List.of(8, 12), List.of(12, 14),
            List.of(12, 16),
            List.of(14, 18), List.of(18, 21), List.of(21, 24),
            List.of(16, 19), List.of(16, 23),
            List.of(9, 13), List.of(13, 17), List.of(17, 20), List.of(20, 23), List.of(23, 25),
            List.of(25, 29), List.of(25, 30)
    );

}