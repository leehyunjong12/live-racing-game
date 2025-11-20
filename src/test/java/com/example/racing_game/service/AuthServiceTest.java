package com.example.racing_game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.racing_game.domain.User;
import com.example.racing_game.dto.RegisterRequest;
import com.example.racing_game.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final RegisterRequest validRequest = new RegisterRequest() {
        @Override
        public String getUsername() { return "testuser"; }
        @Override
        public String getPassword() { return "securepassword"; }
    };

    @Test
    @DisplayName("성공적인 회원가입 시, 비밀번호는 암호화되어 {bcrypt} 접두사와 함께 저장")
    void shouldRegisterNewUserSuccessfully() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("ENCODED_HASH");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.registerNewUser(validRequest);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo("{bcrypt}ENCODED_HASH");
        assertThat(savedUser.getBalance()).isEqualTo(100000);
    }

    @Test
    @DisplayName("이미 존재하는 사용자로 회원가입 시도 시, 예외가 발생")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class,
                () -> authService.registerNewUser(validRequest));

        verify(userRepository, never()).save(any());
    }
}