package com.example.racing_game.service;

import com.example.racing_game.domain.PrizePool;
import com.example.racing_game.domain.User;
import com.example.racing_game.domain.UserCar;
import com.example.racing_game.repository.PrizePoolRepository;
import com.example.racing_game.repository.UserCarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrizeServiceTest {

    @Mock private PrizePoolRepository prizePoolRepository;
    @Mock private UserCarRepository userCarRepository;

    @InjectMocks
    private PrizeService prizeService;

    @Test
    @DisplayName("우승자가 2명이면 상금을 정확히 절반씩 나눠가지고, 차는 삭제")
    void shouldSplitPrizeAndReset() {
        PrizePool pot = new PrizePool();
        pot.setAmount(100_000);
        when(prizePoolRepository.findById(1L)).thenReturn(Optional.of(pot));

        User userA = new User(); userA.setBalance(0);
        User userB = new User(); userB.setBalance(0);

        UserCar carA = new UserCar("A_Car", userA);
        UserCar carB = new UserCar("B_Car", userB);

        when(userCarRepository.findByCarName("A_Car")).thenReturn(Optional.of(carA));
        when(userCarRepository.findByCarName("B_Car")).thenReturn(Optional.of(carB));

        List<String> winners = List.of("A_Car", "B_Car");

        prizeService.awardWinnersAndReset(winners);


        assertThat(userA.getBalance()).isEqualTo(50_000);
        assertThat(userB.getBalance()).isEqualTo(50_000);

        assertThat(pot.getAmount()).isZero();

        verify(userCarRepository).deleteAll();
    }
    @Test
    @DisplayName("Admin과 유저가 공동 우승하면, 유저는 N빵한 금액만 받고 판돈은 초기화")
    void shouldSplitPrizeWithAdmin() {

        PrizePool pot = new PrizePool();
        pot.setAmount(100_000);
        when(prizePoolRepository.findById(1L)).thenReturn(Optional.of(pot));

        User userA = new User();
        userA.setBalance(0);
        UserCar carA = new UserCar("Pobi_1", userA);

        when(userCarRepository.findByCarName("Pobi_1")).thenReturn(Optional.of(carA));

        List<String> winners = List.of("Pobi_1", "Admin_Bot");

        prizeService.awardWinnersAndReset(winners);


        assertThat(userA.getBalance()).isEqualTo(50_000);

        assertThat(pot.getAmount()).isZero();

        verify(userCarRepository).deleteAll();
    }

    @Test
    @DisplayName("Admin이 단독 우승하면 판돈이 초기화되지 않아야 함 (이월)")
    void shouldCarryOverWhenOnlyAdminWins() {
        PrizePool pot = new PrizePool();
        pot.setAmount(100_000);
        when(prizePoolRepository.findById(1L)).thenReturn(Optional.of(pot));

        List<String> winners = List.of("Admin_Bot");

        prizeService.awardWinnersAndReset(winners);

        assertThat(pot.getAmount()).isEqualTo(100_000);
        verify(userCarRepository).deleteAll();
    }
}