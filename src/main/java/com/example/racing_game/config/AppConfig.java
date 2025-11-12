package com.example.racing_game.config;

import com.example.racing_game.controller.RaceBroadcastHandler;
import com.example.racing_game.domain.GameRuleEngine;
import com.example.racing_game.domain.MoveStrategy;
import com.example.racing_game.domain.RandomMoveStrategy;
import com.example.racing_game.domain.TrackLayout;
import com.example.racing_game.service.RaceService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableAsync
public class AppConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(raceBroadcastHandler(), "/ws/race")
                .setAllowedOrigins("*");
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public GameRuleEngine gameRuleEngine() {
        return new GameRuleEngine();
    }

    @Bean
    public TrackLayout trackLayout() {
        return new TrackLayout();
    }

    @Bean
    public MoveStrategy moveStrategy() {
        return new RandomMoveStrategy();
    }

    @Bean
    public RaceService raceService() {
        return new RaceService(objectMapper(), moveStrategy(), gameRuleEngine());
    }

    @Bean
    public RaceBroadcastHandler raceBroadcastHandler() {
        return new RaceBroadcastHandler(raceService(), trackLayout(), objectMapper());
    }
}