package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

import static io.battlesnake.starter.utils.MovementUtils.findNextMovement;

public class FoodPathSolver implements PathSolver {

    @Override
    public String findNextStep(GameBoard gameBoard) {
    
        return findNextMovement(gameBoard, gameBoard.getMe().getHead());
    }
    
    
    /* ########################## Lock Area ########################## */
    
    private Vertex lockArea(int[][] board, Vertex start) {
        return null;
        
    }
    
    /* ######################################################################### */
}
