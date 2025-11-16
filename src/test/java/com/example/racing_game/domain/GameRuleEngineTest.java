package com.example.racing_game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.racing_game.dto.RuleResult;
import org.apache.tomcat.util.digester.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
public class GameRuleEngineTest {

    @Mock
    private Random random;

    @InjectMocks
    private GameRuleEngine gameRuleEngine;

    @Test
    @DisplayName("이동 실패(shouldMove=false) 시, 제자리를 반환")
    void shouldReturnCurrentPositionWhenMoveIsFalse() {
        int currentPosition = 5;

        RuleResult result = gameRuleEngine.getNextPosition(currentPosition, false);

        assertThat(result.nextPosition()).isEqualTo(currentPosition);
        assertThat(result.penaltyTurns()).isZero();
    }

    @Test
    @DisplayName("결승선(30) 도착 시, 더 이상 이동하지 않아야 함")
    void shouldStayAtFinishLine() {
        int finishLine = MapDataStorage.TRACK_LENGTH;

        RuleResult result = gameRuleEngine.getNextPosition(finishLine, true);

        assertThat(result.nextPosition()).isEqualTo(finishLine);
        assertThat(result.penaltyTurns()).isZero();
    }

    @Test
    @DisplayName("일반 노드(0번)는 ADJACENCY_LIST를 따라 다음 노드(1번)로 이동")
    void shouldMoveToNextNodeOnNormalTile() {
        RuleResult result = gameRuleEngine.getNextPosition(0, true);

        assertThat(result.nextPosition()).isEqualTo(1);
        assertThat(result.penaltyTurns()).isZero();
    }

    @Test
    @DisplayName("갈림길(1번)에서 9번(3번째 선택지)이 선택")
    void shouldChooseCorrectPathAtJunction() {
        when(random.nextInt(3)).thenReturn(2);

        RuleResult result = gameRuleEngine.getNextPosition(1, true);

        assertThat(result.nextPosition()).isEqualTo(9);
        assertThat(result.penaltyTurns()).isZero();
    }

    @Test
    @DisplayName("JAIL(23번)을 밟으면, 30% 확률로 2턴 페널티를 받음")
    void shouldStayAndGetPenaltyOnObstacle() {
        when(random.nextInt(1)).thenReturn(0);
        when(random.nextInt(100)).thenReturn(20);
        RuleResult result = gameRuleEngine.getNextPosition(20, true);

        assertThat(result.nextPosition()).isEqualTo(23);
        assertThat(result.penaltyTurns()).isEqualTo(2);
    }

    @Test
    @DisplayName("JAIL(23번)을 밟으면, 70% 확률로 페널티를 받지 않음")
    void shouldNotGetPenaltyOnJailWith70PercentChance() {

        when(random.nextInt(1)).thenReturn(0);

        when(random.nextInt(100)).thenReturn(50);

        RuleResult result = gameRuleEngine.getNextPosition(20, true);

        assertThat(result.nextPosition()).isEqualTo(23);
        assertThat(result.penaltyTurns()).isEqualTo(0);
    }

    @Test
    @DisplayName("MOVE_BACK_NODE(6번)을 밟으면, 30% 확률로 4번(6-2)으로 이동")
    void shouldMoveBackOnMoveBackNode() {
        when(random.nextInt(1)).thenReturn(0);
        when(random.nextInt(100)).thenReturn(20);

        RuleResult result = gameRuleEngine.getNextPosition(3, true);

        assertThat(result.nextPosition()).isEqualTo(4);
        assertThat(result.penaltyTurns()).isEqualTo(0);
    }

    @Test
    @DisplayName("MOVE_BACK_NODE(6번)을 밟아도, 70% 확률로 6번에 머뭄")
    void shouldStayOnMoveBackNodeWith70PercentChance() {
        when(random.nextInt(1)).thenReturn(0);
        when(random.nextInt(100)).thenReturn(50);

        RuleResult result = gameRuleEngine.getNextPosition(3, true);

        assertThat(result.nextPosition()).isEqualTo(6);
        assertThat(result.penaltyTurns()).isEqualTo(0);
    }

    @Test
    @DisplayName("MOVE_TO_START(29)를 밟으면, 0번으로 이동")
    void shouldMoveToStartOnMoveToStartNode() {
        when(random.nextInt(2)).thenReturn(1);

        RuleResult result = gameRuleEngine.getNextPosition(25, true);

        assertThat(result.nextPosition()).isEqualTo(0);
        assertThat(result.penaltyTurns()).isZero();
    }
    @Test
    @DisplayName("MOVE_TO_MIDPOINTS(16번)를 밟으면, 10~20 사이의 17번으로 이동")
    void shouldMoveToMidpointOnMoveToMidpointsNode() {
        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(10, 21)).thenReturn(17);

        RuleResult result = gameRuleEngine.getNextPosition(24, true);

        assertThat(result.nextPosition()).isEqualTo(17);
        assertThat(result.penaltyTurns()).isZero();
    }
}