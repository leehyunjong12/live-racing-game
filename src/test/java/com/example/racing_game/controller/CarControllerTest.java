package com.example.racing_game.controller;

import com.example.racing_game.dto.CarRegistrationRequest;
import com.example.racing_game.service.CarService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarService carService;

    @Test
    @DisplayName("로그인한 사용자는 자동차를 등록할 수 있음 (200 OK)")
    @WithMockUser(username = "racer")
    void shouldRegisterCarsSuccessfully() throws Exception {
        Map<String, Integer> requestMap = Map.of("quantity", 3);

        mockMvc.perform(post("/api/cars/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("자동차 등록 완료!"));

        verify(carService).registerCars("racer", 3);
    }

    @Test
    @DisplayName("잔액 부족 등으로 서비스에서 에러가 나면 400 Bad Request를 반환")
    @WithMockUser(username = "tester")
    void shouldReturnBadRequestWhenServiceFails() throws Exception {
        doThrow(new IllegalArgumentException("잔액 부족"))
                .when(carService).registerCars(anyString(), anyInt());

        Map<String, Integer> requestMap = Map.of("quantity", 5);

        mockMvc.perform(post("/api/cars/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("잔액 부족"));
    }
}