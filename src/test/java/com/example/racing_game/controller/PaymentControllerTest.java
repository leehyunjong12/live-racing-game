package com.example.racing_game.controller;

import com.example.racing_game.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("로그인한 사용자는 잔액 충전을 할 수 있다 (200 OK)")
    @WithMockUser(username = "rich_man")
    void shouldChargeBalance() throws Exception {
        given(userService.chargeBalance(anyString(), anyLong())).willReturn(150000L);

        Map<String, Long> requestMap = Map.of("amount", 50000L);

        mockMvc.perform(post("/api/payment/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("충전 성공"))
                .andExpect(jsonPath("$.balance").value(150000));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 충전을 할 수 없다 (403 Forbidden)")
    void shouldFailChargeWhenNotLoggedIn() throws Exception {

        Map<String, Long> requestMap = Map.of("amount", 50000L);

        mockMvc.perform(post("/api/payment/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인한 사용자는 잔액 조회를 할 수 있다")
    @WithMockUser(username = "rich_man")
    void shouldGetBalance() throws Exception {
        given(userService.getBalance("rich_man")).willReturn(9999L);

        mockMvc.perform(get("/api/payment/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(9999));
    }
}