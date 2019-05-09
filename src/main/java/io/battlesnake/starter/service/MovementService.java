package io.battlesnake.starter.service;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

public interface MovementService {
    
    String findNextMovement(GameBoard gameBoard, Vertex me);
}
