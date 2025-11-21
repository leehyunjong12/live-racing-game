package com.example.racing_game.controller;

import com.example.racing_game.domain.TrackLayout;
import com.example.racing_game.dto.MapLayoutDto;
import com.example.racing_game.repository.UserCarRepository;
import com.example.racing_game.service.RaceService;
import com.example.racing_game.domain.UserCar;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();

        if (payload.startsWith("START:")) {
            try {
                String[] parts = payload.split(":");
                int rounds = Integer.parseInt(parts[parts.length - 1]);

                List<UserCar> dbCars = userCarRepository.findAll();

                List<String> carNames = new ArrayList<>(
                        dbCars.stream().map(UserCar::getCarName).toList()
                );

                carNames.add(0, "Admin_Bot");

                raceService.startAsyncRace(carNames, rounds, this::broadcast);

            } catch (Exception e) {
                System.err.println("경주 시작 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
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