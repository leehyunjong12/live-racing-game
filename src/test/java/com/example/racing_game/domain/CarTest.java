package com.example.racing_game.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {
    @Test
    @DisplayName("Car는 생성 시 초기화 확인")
    void shouldInitializeWithCorrectDefaults() {
        Car car = new Car("Pobi");

        assertThat(car.getName()).isEqualTo("Pobi");
        assertThat(car.getPosition()).isZero();
        assertThat(car.getTurnsToSkip()).isZero();
    }
    @Test
    @DisplayName("isSkippingTurn 제대로 반환")
    void shouldReturnTrueOnlyWhenSkipTurnsArePositive() {
        Car car = new Car("Crong");

        car.setTurnsToSkip(1);
        assertThat(car.isSkippingTurn()).isTrue();

        car.setTurnsToSkip(0);
        assertThat(car.isSkippingTurn()).isFalse();
    }
    @Test
    @DisplayName("consumeSkipTurn 동작 확인")
    void shouldDecrementSkipTurnsButNotBelowZero() {
        Car car = new Car("Honux");
        car.setTurnsToSkip(2);

        car.consumeSkipTurn();
        assertThat(car.getTurnsToSkip()).isEqualTo(1);

        car.consumeSkipTurn();
        assertThat(car.getTurnsToSkip()).isEqualTo(0);

        car.consumeSkipTurn();
        assertThat(car.getTurnsToSkip()).isZero();
    }
}
