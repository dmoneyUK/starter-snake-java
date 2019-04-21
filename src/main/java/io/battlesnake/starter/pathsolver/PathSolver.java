package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.List;
import java.util.Map;

public interface PathSolver {
    
    //List<Vertex> findPath(int[][] board, List<Vertex> foodList, Vertex start);
    
    //Map<String, int[][]> calculateDistanceForAllSnakes(int[][] board, List<int[]> foodList, Map<String,int[]> snakeHeads);
    
    String findNextStep(GameBoard gameBoard);
}
