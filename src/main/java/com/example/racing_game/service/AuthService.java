package com.example.racing_game.service;

import com.example.racing_game.domain.User;
import com.example.racing_game.dto.RegisterRequest;
import com.example.racing_game.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User registerNewUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        String prefixedPassword = "{bcrypt}" + encodedPassword;

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(prefixedPassword);

        return userRepository.save(newUser);
    }
    public Authentication login(String username, String password) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(token);
    }
}