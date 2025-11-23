package com.example.racing_game.controller;

import com.example.racing_game.domain.TrackLayout;
import com.example.racing_game.dto.MapLayoutDto;
import com.example.racing_game.repository.UserCarRepository;
import com.example.racing_game.service.RaceService;
import com.example.racing_game.domain.UserCar;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RaceBroadcastHandler extends TextWebSocketHandler {

    private final RaceService raceService;
    private final UserCarRepository userCarRepository;
    private final TrackLayout trackLayout;
    private final ObjectMapper objectMapper;
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public RaceBroadcastHandler(RaceService raceService, TrackLayout trackLayout, ObjectMapper objectMapper, UserCarRepository userCarRepository) {
        this.raceService = raceService;
        this.trackLayout = trackLayout;
        this.objectMapper = objectMapper;
        this.userCarRepository = userCarRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("새 클라이언트 접속: " + session.getId());

        MapLayoutDto mapLayout = trackLayout.getMapLayout();

        String mapJson = objectMapper.writeValueAsString(mapLayout);
        session.sendMessage(new TextMessage(mapJson));
    }
    @Scheduled(cron = "0 */5 * * * *")
    public void startScheduledRace() {
        try {
            System.out.println("--- ⏰ 5분 스케줄링: 자동 경주 시작 ---");
            broadcast("{\"type\":\"INFO\", \"message\":\"잠시 후 경주가 시작됩니다!\"}");

            List<UserCar> dbCars = userCarRepository.findAll();
            List<String> carNames = new ArrayList<>(
                    dbCars.stream().map(UserCar::getCarName).toList()
            );

            carNames.add(0, "Admin_Bot");
            int rounds = 50;
            raceService.startAsyncRace(carNames, rounds, this::broadcast);

        } catch (Exception e) {
            System.err.println("스케줄링 오류: " + e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("클라이언트 메시지 수신(무시됨): " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) { /* ... */ }
        });
    }
}