package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;
import io.battlesnake.starter.service.strategy.StrategyService;

import java.util.Map;

import static io.battlesnake.starter.utils.DistanceBoardUtils.getAllSnakesDistanceBoards;
import static io.battlesnake.starter.utils.MovementUtils.backTrack;

public class PathSolverImpl implements PathSolver {
    
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    @Override
    public String findNextStep(GameBoard gameBoard) {
    
        Vertex me = gameBoard.getMe().getHead();
        Vertex target = findNextPos(gameBoard, me);
        
    
        int y = target.getRow() - me.getRow();
        int x = target.getColumn() - me.getColumn();
    
        String nextStep;
    
        if (y == -1 && x == 0) {
            nextStep = "up";
        } else if (y == 1 && x == 0) {
            nextStep = "down";
        } else if (y == 0 && x == 1) {
            nextStep = "right";
        } else {
            nextStep = "left";
        }
        return nextStep;
    }
    
    // Find the location (a target) to move to.
    private Vertex findNextPos(GameBoard gameBoard, Vertex currentPos) {
        Vertex nextPos = null;
        
        // Calculate the distance board for all snakes
        Map<Vertex, int[][]> snakesDistanceMap = getAllSnakesDistanceBoards(gameBoard);
        int[][] myDistanceBoard = snakesDistanceMap.get(currentPos);
        
        StrategyService strategyService = new StrategyService();
        Vertex target = strategyService.makeDecision(gameBoard, snakesDistanceMap);
        
        return backTrack(myDistanceBoard, target);
    }
    
}
