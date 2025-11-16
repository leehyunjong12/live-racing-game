package com.example.racing_game.domain;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {

    private final Random random = new Random();

    @Override
    public boolean shouldMove() {
        return random.nextInt(10) >= 2;
    }

}