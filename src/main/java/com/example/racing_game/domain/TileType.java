package com.example.racing_game.domain;

public enum TileType {
    NORMAL,       // 일반
    OBSTACLE,     // 장애물 (2턴 쉼)
    MOVE_BACK_NODE // 확률적으로 현재 노드 번호 -2로 이동
}