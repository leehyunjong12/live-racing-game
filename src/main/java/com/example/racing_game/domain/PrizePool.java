package com.example.racing_game.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "prize_pool")
public class PrizePool {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private long amount = 0;

    public PrizePool() {}
}