package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;
import io.battlesnake.starter.service.StrategyService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import static io.battlesnake.starter.utils.DistanceBoardUtils.getRiskyAllSnakesDistanceBoards;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getSafeAllSnakesDistanceBoards;
import static io.battlesnake.starter.utils.MovementUtils.backTrack;

@Slf4j
public class PathSolverImpl implements PathSolver {
    
    private final StrategyService strategyService;
    
    public PathSolverImpl(StrategyService strategyService) {
        this.strategyService = strategyService;
    }
    
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
    
        // Calculate the distance board for all snakes
        //CompletableFuture<Vertex> safeFuture = FutureHelper.asyncExecute(() -> {
        //    Map<Vertex, int[][]> safeSnakesDistanceBoards = getSafeAllSnakesDistanceBoards(gameBoard);
        //    int[][] mySafeDistanceBoard = safeSnakesDistanceBoards.get(currentPos);
        //    return strategyService.makeDecision(gameBoard, safeSnakesDistanceBoards)
        //                          .map(target -> backTrack(mySafeDistanceBoard, target))
        //                          .orElse(null);
        //});
    
        //CompletableFuture<Vertex> riskyFuture = FutureHelper.asyncExecute(() -> {
        //    Map<Vertex, int[][]> riskySnakesDistanceBoards = getRiskyAllSnakesDistanceBoards(gameBoard);
        //    int[][] myRiskDistanceBoard = riskySnakesDistanceBoards.get(currentPos);
        //    return strategyService.makeDecision(gameBoard, riskySnakesDistanceBoards)
        //                          .map(target -> backTrack(myRiskDistanceBoard, target))
        //                          .orElse(null);
        //});
    
        Map<Vertex, int[][]> safeSnakesDistanceBoards = getSafeAllSnakesDistanceBoards(gameBoard);
        int[][] mySafeDistanceBoard = safeSnakesDistanceBoards.get(currentPos);
        Vertex safeOption = strategyService.makeDecision(gameBoard, safeSnakesDistanceBoards)
                                           .map(target -> backTrack(mySafeDistanceBoard, target))
                                           .orElse(null);
    
        Map<Vertex, int[][]> riskySnakesDistanceBoards = getRiskyAllSnakesDistanceBoards(gameBoard);
        int[][] myRiskDistanceBoard = riskySnakesDistanceBoards.get(currentPos);
        Vertex riskyOption = strategyService.makeDecision(gameBoard, riskySnakesDistanceBoards)
                                            .map(target -> backTrack(myRiskDistanceBoard, target))
                                            .orElse(null);
    
        return Optional.ofNullable(safeOption).orElse(riskyOption);
    }
    
}
