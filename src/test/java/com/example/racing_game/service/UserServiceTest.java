package com.example.racing_game.service;

import com.example.racing_game.domain.User;
import com.example.racing_game.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("정상적인 금액 충전 시 잔액이 증가")
    void shouldChargeBalanceSuccessfully() {
        User user = new User();
        user.setUsername("tester");
        user.setBalance(10000);

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));

        long newBalance = userService.chargeBalance("tester", 50000);

        assertThat(newBalance).isEqualTo(60000);
        assertThat(user.getBalance()).isEqualTo(60000);
    }

    @Test
    @DisplayName("0원 이하의 금액을 충전하려고 하면 예외가 발생")
    void shouldThrowExceptionForInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.chargeBalance("tester", -5000));
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 충전 시도 시 예외가 발생")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.chargeBalance("ghost", 10000));
    }
}