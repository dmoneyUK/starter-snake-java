package io.battlesnake.starter.service.strategy;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class GameState {
    private GameBoard gameBoard;
    private Map<Vertex, int[][]> snakesDistanceBoardMap;
    
}
