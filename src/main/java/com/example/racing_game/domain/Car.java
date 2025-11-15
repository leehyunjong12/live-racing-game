package com.example.racing_game.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Car {
    private final String name;
    @Setter
    private int position = 0;
    @Getter
    @Setter
    private int turnsToSkip = 0;

    public Car(String name) {
        this.name = name;
    }

    public void consumeSkipTurn() {
        if (this.turnsToSkip > 0) {
            this.turnsToSkip--;
        }
    }
    public boolean isSkippingTurn() {
        return this.turnsToSkip > 0;
    }
}