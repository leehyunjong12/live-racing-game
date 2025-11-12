package com.example.racing_game.dto;

import com.example.racing_game.domain.TileType;

public record MapNode(
        int id,
        int x,
        int y,
        TileType type
) {}