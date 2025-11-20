package com.example.racing_game.service;

import com.example.racing_game.domain.User;
import com.example.racing_game.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JpaUserDetailsServiceTest {

    @Autowired
    JpaUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final String TEST_USERNAME = "security_user";
    private final String TEST_PASSWORD = "testpassword";



    @Test
    @DisplayName("DB에 존재하는 사용자 이름으로 UserDetails를 성공적으로 로드")
    void shouldLoadUserSuccessfully() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setBalance(0);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USERNAME);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("DB에 없는 사용자 이름으로 로드 시 UsernameNotFoundException을 던짐")
    void shouldThrowExceptionWhenUserNotFound() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setBalance(0);
        userRepository.save(user);
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("non_existent_user"));
    }
}