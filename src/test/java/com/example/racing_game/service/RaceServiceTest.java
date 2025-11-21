package com.example.racing_game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.example.racing_game.domain.GameRuleEngine;
import com.example.racing_game.domain.MapDataStorage;
import com.example.racing_game.domain.MoveStrategy;
import com.example.racing_game.dto.RuleResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
@ExtendWith(MockitoExtension.class)
public class RaceServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MoveStrategy moveStrategy;

    @Mock
    private GameRuleEngine gameRuleEngine;

    @Mock
    private PrizeService prizeService;

    @InjectMocks
    private RaceService raceService;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    @Test
    @DisplayName("경주 시작 시, 0라운드 상태를 정확히 방송")
    void shouldBroadcastRoundZeroStateFirst() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi");
        int rounds = 1;
        Consumer<String> broadcaster = (json) -> {};

        when(gameRuleEngine.getNextPosition(anyInt(), anyBoolean())).thenReturn(new RuleResult(0, 0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        raceService.startAsyncRace(carNames, rounds, broadcaster);

        verify(objectMapper, times(3)).writeValueAsString(mapCaptor.capture());

        Map<String, Object> roundZeroMap = mapCaptor.getAllValues().get(0);

        assertThat(roundZeroMap.get("type")).isEqualTo("RACING");
        assertThat(roundZeroMap.get("round")).isEqualTo(0);

        List<Map<String, Object>> cars = (List<Map<String, Object>>) roundZeroMap.get("cars");
        assertThat(cars).hasSize(1);
        assertThat(cars.get(0).get("name")).isEqualTo("Pobi");
        assertThat(cars.get(0).get("position")).isEqualTo(0);
    }

    @Test
    @DisplayName("차가 '턴 스킵' 상태일 때, 다음 두 턴 이동 로직은 실행되지 않는다")
    void shouldSkipTurnAndNotMoveWhenCarIsSkipping() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi");
        int rounds = 3;
        Consumer<String> broadcaster = (json) -> {};

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(moveStrategy.shouldMove()).thenReturn(true);
        when(gameRuleEngine.getNextPosition(0, true))
                .thenReturn(new RuleResult(8, 2));
        raceService.startAsyncRace(carNames, rounds, broadcaster);

        verify(gameRuleEngine, times(1)).getNextPosition(0, true);
        verify(gameRuleEngine, never()).getNextPosition(8, true);
    }

    @Test
    @DisplayName("차가 이동(shouldMove=true)할 때, 룰 엔진의 결과를 반영")
    void shouldApplyRuleResultWhenMoveIsTrue() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi");
        int rounds = 1;
        List<String> broadcastLog = new ArrayList<>();
        Consumer<String> broadcaster = broadcastLog::add;

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"position\":5}");
        when(moveStrategy.shouldMove()).thenReturn(true);
        when(gameRuleEngine.getNextPosition(0, true))
                .thenReturn(new RuleResult(5, 0));

        raceService.startAsyncRace(carNames, rounds, broadcaster);

        assertThat(broadcastLog.get(1)).isEqualTo("{\"position\":5}");
        verify(gameRuleEngine, times(1)).getNextPosition(0, true);
    }

    @Test
    @DisplayName("경주가 끝나면(isRaceFinished=true), 우승자 JSON을 정확히 방송")
    void shouldBreakLoopWhenRaceIsFinished() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi", "Crong");
        int rounds = 10;
        Consumer<String> broadcaster = (json) -> {};
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(moveStrategy.shouldMove()).thenReturn(true);
        when(gameRuleEngine.getNextPosition(anyInt(), anyBoolean()))
                .thenReturn(new RuleResult(MapDataStorage.TRACK_LENGTH, 0),
                        new RuleResult(0, 0));

        raceService.startAsyncRace(carNames, rounds, broadcaster);

        verify(objectMapper, times(3)).writeValueAsString(captor.capture());

        Map<String, Object> winnerMap = captor.getAllValues().get(2);

        assertThat(winnerMap.get("type")).isEqualTo("WINNER");

        List<String> winners = (List<String>) winnerMap.get("winners");
        assertThat(winners).containsExactly("Pobi");
    }
    @Test
    @DisplayName("라운드가 끝나도 결승선에 아무도 도착하지 못하면, 빈 우승자 리스트를 방송")
    void shouldBroadcastEmptyWinnerListWhenNoOneFinishes() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi", "Crong");
        int rounds = 2;
        Consumer<String> broadcaster = (json) -> {};

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(moveStrategy.shouldMove()).thenReturn(true);
        when(gameRuleEngine.getNextPosition(anyInt(), anyBoolean()))
                .thenReturn(new RuleResult(5, 0));

        raceService.startAsyncRace(carNames, rounds, broadcaster);

        verify(objectMapper, times(4)).writeValueAsString(captor.capture());

        Map<String, Object> winnerMap = captor.getAllValues().get(3);
        assertThat(winnerMap.get("type")).isEqualTo("WINNER");

        List<String> winners = (List<String>) winnerMap.get("winners");
        assertThat(winners).isEmpty();
    }@Test
    @DisplayName("경주 종료 시 PrizeService를 호출하여 정산하고, 그 후에 방송")
    void shouldCallPrizeServiceBeforeBroadcastingWinner() throws JsonProcessingException {
        List<String> carNames = List.of("Pobi");
        int rounds = 10;
        Consumer<String> broadcaster = mock(Consumer.class);

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(moveStrategy.shouldMove()).thenReturn(true);

        when(gameRuleEngine.getNextPosition(eq(0), eq(true)))
                .thenReturn(new RuleResult(MapDataStorage.TRACK_LENGTH, 0));

        raceService.startAsyncRace(carNames, rounds, broadcaster);


        var inOrder = inOrder(prizeService, broadcaster);

        inOrder.verify(prizeService).awardWinnersAndReset(anyList());
        inOrder.verify(broadcaster).accept(anyString());
    }

}