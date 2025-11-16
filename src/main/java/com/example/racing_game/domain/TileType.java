package com.example.racing_game.domain;

public enum TileType {
    NORMAL,       // 일반
    JAIL,     // 감옥 (2턴 쉼)
    MOVE_BACK_NODE, // 확률적으로 현재 노드 번호 -2로 이동
    MOVE_TO_START, //시작 포인트로 이동
    MOVE_TO_MIDPOINTS,//중간 포인트중 랜덤 이동(10~20)
    SLIDE // 후반 노드로 슬라이드
}