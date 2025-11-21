package com.example.racing_game.config;

import com.example.racing_game.controller.RaceBroadcastHandler;
import com.example.racing_game.domain.GameRuleEngine;
import com.example.racing_game.domain.MoveStrategy;
import com.example.racing_game.domain.RandomMoveStrategy;
import com.example.racing_game.domain.TrackLayout;
import com.example.racing_game.service.RaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.racing_game.repository.UserCarRepository;
import com.example.racing_game.repository.PrizePoolRepository;
import com.example.racing_game.service.PrizeService;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableAsync
@RequiredArgsConstructor
public class AppConfig implements WebSocketConfigurer {

    private final UserCarRepository userCarRepository;
    private final PrizePoolRepository prizePoolRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(raceBroadcastHandler(userCarRepository), "/ws/race")
                .setAllowedOrigins("*");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Random random() {return new Random();}

    @Bean
    public GameRuleEngine gameRuleEngine() {
        return new GameRuleEngine(random());
    }

    @Bean
    public TrackLayout trackLayout() {
        return new TrackLayout();
    }

    @Bean
    public MoveStrategy moveStrategy() {
        return new RandomMoveStrategy(random());
    }

    @Bean
    public PrizeService prizeService() {
        return new PrizeService(prizePoolRepository, userCarRepository);
    }

    @Bean
    public RaceService raceService() {
        return new RaceService(objectMapper(), moveStrategy(), gameRuleEngine(), prizeService());
    }

    @Bean
    public RaceBroadcastHandler raceBroadcastHandler(UserCarRepository repo) {
        return new RaceBroadcastHandler(raceService(), trackLayout(), objectMapper(),repo);
    }
}