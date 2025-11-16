package com.example.racing_game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.racing_game.dto.MapLayoutDto;
import com.example.racing_game.dto.MapNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

public class TrackLayoutTest {

    private TrackLayout trackLayout;
    private MapLayoutDto mapLayoutDto;

    @BeforeEach
    void setUp() {
        trackLayout = new TrackLayout();
        mapLayoutDto = trackLayout.getMapLayout();
    }

    @Test
    @DisplayName("DTO의 타입이 'MAP_LAYOUT'")
    void shouldHaveCorrectDtoType() {
        assertThat(mapLayoutDto.type()).isEqualTo("MAP_LAYOUT");
    }

    @Test
    @DisplayName("DTO의 노드 개수가 원본 데이터와 일치")
    void shouldHaveSameNodeCountAsDataSource() {
        assertThat(mapLayoutDto.nodes()).hasSize(MapDataStorage.LAYOUT_COORDS.size());
    }

    @Test
    @DisplayName("DTO의 노드에 특수 타일 속성이 정확히 포함")
    void shouldIncludeCorrectTileTypesInNodes() {

        MapNode node23 = mapLayoutDto.nodes().get(23);
        assertThat(node23.id()).isEqualTo(23);
        assertThat(node23.type()).isEqualTo(TileType.JAIL);

        MapNode node1 = mapLayoutDto.nodes().get(1);
        assertThat(node1.id()).isEqualTo(1);
        assertThat(node1.type()).isEqualTo(TileType.NORMAL);

        MapNode node6 = mapLayoutDto.nodes().get(6);
        assertThat(node6.id()).isEqualTo(6);
        assertThat(node6.type()).isEqualTo(TileType.MOVE_BACK_NODE);
    }

    @Test
    @DisplayName("DTO의 노드에 (x, y) 좌표가 정확히 포함")
    void shouldIncludeCorrectCoordinatesInNodes() {
        int[] expectedCoords = MapDataStorage.LAYOUT_COORDS.get(0);

        MapNode node0 = mapLayoutDto.nodes().get(0);
        assertThat(node0.x()).isEqualTo(expectedCoords[0]);
        assertThat(node0.y()).isEqualTo(expectedCoords[1]);
    }

    @Test
    @DisplayName("DTO의 선(Line) 목록이 원본 데이터와 일치해야 한다")
    void shouldHaveSameLinesAsDataSource() {
        assertThat(mapLayoutDto.lines()).hasSize(MapDataStorage.LAYOUT_LINES.size());
        assertThat(mapLayoutDto.lines()).isEqualTo(MapDataStorage.LAYOUT_LINES);
    }
}