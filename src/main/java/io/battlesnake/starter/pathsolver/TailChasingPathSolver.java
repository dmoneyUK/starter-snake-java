package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.service.MovementService;

public class TailChasingPathSolver implements PathSolver {
    
    @Override
    public String findNextStep(GameBoard gameBoard) {
        
        //return movementService.findNextMovement(gameBoard, gameBoard.getMe().getHead());
        return null;
    }
}
