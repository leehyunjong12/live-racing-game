package com.example.racing_game.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.racing_game.domain.User;
import com.example.racing_game.dto.RegisterRequest;
import com.example.racing_game.repository.UserCarRepository;
import com.example.racing_game.repository.UserRepository;
import com.example.racing_game.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserCarRepository userCarRepository;
    @Test
    @DisplayName("회원가입 요청 시 201 Created를 반환")
    void shouldRegisterUser() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("newuser");
        given(authService.registerNewUser(any(RegisterRequest.class))).willReturn(mockUser);

        RegisterRequest request = new RegisterRequest();
        String jsonRequest = "{\"username\":\"newuser\", \"password\":\"1234\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User registered successfully")));
    }

    @Test
    @DisplayName("로그인 성공 시 200 OK를 반환")
    void shouldLoginSuccessfully() throws Exception {
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("user", "pw");
        given(authService.login(anyString(), anyString())).willReturn(mockAuth);

        String jsonRequest = "{\"username\":\"user\", \"password\":\"pw\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("로그인 성공")));
    }

    @Test
    @DisplayName("로그인 실패 시 401 Unauthorized를 반환")
    void shouldFailLogin() throws Exception {
        given(authService.login(anyString(), anyString()))
                .willThrow(new BadCredentialsException("Fail"));

        String jsonRequest = "{\"username\":\"user\", \"password\":\"wrong\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인된 사용자가 로그아웃 시 200 OK를 반환")
    @WithMockUser
    void shouldLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 성공"));
    }
    @Test
    @DisplayName("로그인된 사용자는 자신의 정보와 자동차 대수를 조회 (/me)")
    @WithMockUser(username = "racer") // 가짜 로그인 상태
    void shouldReturnCurrentUserAndCarCount() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("racer");

        when(userRepository.findByUsername("racer")).thenReturn(Optional.of(mockUser));

        when(userCarRepository.countByOwner(mockUser)).thenReturn(5L);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("racer"))
                .andExpect(jsonPath("$.carCount").value(5));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 /me 요청 시 401 Unauthorized를 반환")
    void shouldReturnUnauthorizedForAnonymousUser() throws Exception {

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}