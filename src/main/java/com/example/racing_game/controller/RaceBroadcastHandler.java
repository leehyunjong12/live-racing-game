package com.example.racing_game.controller;

import com.example.racing_game.domain.TrackLayout;
import com.example.racing_game.dto.MapLayoutDto;
import com.example.racing_game.service.RaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RaceBroadcastHandler extends TextWebSocketHandler {

    private final RaceService raceService;
    private final TrackLayout trackLayout;
    private final ObjectMapper objectMapper;
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public RaceBroadcastHandler(RaceService raceService, TrackLayout trackLayout, ObjectMapper objectMapper) {
        this.raceService = raceService;
        this.trackLayout = trackLayout;
        this.objectMapper = objectMapper;
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
                List<String> carNames = List.of(parts[1].split(","));
                int rounds = Integer.parseInt(parts[2]);
                raceService.startAsyncRace(carNames, rounds, this::broadcast);
            } catch (Exception e) {
                System.err.println("잘못된 START 메시지 형식입니다: " + payload);
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