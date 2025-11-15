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
    @DisplayName("OBSTACLE(23번)을 밟으면, 23번으로 이동하고 1턴 페널티를 받음")
    void shouldStayAndGetPenaltyOnObstacle() {
        RuleResult result = gameRuleEngine.getNextPosition(20, true);

        assertThat(result.nextPosition()).isEqualTo(23);
        assertThat(result.penaltyTurns()).isEqualTo(1);
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
    @DisplayName("막다른 길(26번)에 도착하면, 제자리를 반환")
    void shouldStayAtDeadEndNode() {
        RuleResult result = gameRuleEngine.getNextPosition(26, true);

        assertThat(result.nextPosition()).isEqualTo(26);
        assertThat(result.penaltyTurns()).isEqualTo(0);
    }
}