package com.example.racing_game.service;

import com.example.racing_game.domain.PrizePool;
import com.example.racing_game.domain.User;
import com.example.racing_game.domain.UserCar;
import com.example.racing_game.repository.PrizePoolRepository;
import com.example.racing_game.repository.UserCarRepository;
import com.example.racing_game.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCarRepository userCarRepository;
    @Mock
    private PrizePoolRepository prizePoolRepository;

    @InjectMocks
    private CarService carService;

    @Test
    @DisplayName("자동차 2대를 등록하면 잔액이 10만원 차감되고, 상금이 10만원 증가")
    void shouldDeductBalanceAndIncreasePrizePool() {
        User user = new User();
        user.setUsername("racer");
        user.setBalance(200_000);

        PrizePool prizePool = new PrizePool();
        prizePool.setAmount(0);

        when(userRepository.findByUsername("racer")).thenReturn(Optional.of(user));
        when(prizePoolRepository.findById(1L)).thenReturn(Optional.of(prizePool));
        when(userCarRepository.countByOwner(user)).thenReturn(0L);

        carService.registerCars("racer", 2);

        assertThat(user.getBalance()).isEqualTo(100_000);
        assertThat(prizePool.getAmount()).isEqualTo(100_000);
    }

    @Test
    @DisplayName("자동차 이름은 '닉네임_순번' 형식으로 자동 생성")
    void shouldGenerateCorrectCarNames() {
        User user = new User();
        user.setUsername("pobi");
        user.setBalance(100000);

        when(userRepository.findByUsername("pobi")).thenReturn(Optional.of(user));
        when(userCarRepository.countByOwner(user)).thenReturn(3L);
        when(prizePoolRepository.findById(1L)).thenReturn(Optional.of(new PrizePool()));

        carService.registerCars("pobi", 2);

        ArgumentCaptor<UserCar> captor = ArgumentCaptor.forClass(UserCar.class);

        verify(userCarRepository, times(2)).save(captor.capture());

        List<UserCar> savedCars = captor.getAllValues();

        assertThat(savedCars).hasSize(2);
        assertThat(savedCars.get(0).getCarName()).isEqualTo("pobi_4");
        assertThat(savedCars.get(1).getCarName()).isEqualTo("pobi_5");
    }

    @Test
    @DisplayName("잔액이 부족하면 예외가 발생하고 등록되지 않아야 함")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        User user = new User();
        user.setUsername("beggar");
        user.setBalance(40000);

        when(userRepository.findByUsername("beggar")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> carService.registerCars("beggar", 1));
        verify(userCarRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 요청하면 예외가 발생")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> carService.registerCars("ghost", 1));
    }
}