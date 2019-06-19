package io.battlesnake.starter.service.strategy;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static io.battlesnake.starter.service.strategy.StrategyResult.STRATEGY_FAILURE;
import static io.battlesnake.starter.utils.DistanceBoardUtils.findAvailableSpaceNearby;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getDistance;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getFarthestVertex;
import static io.battlesnake.starter.utils.GameBoardUtils.findEmptyNeighberVertex;
import static io.battlesnake.starter.utils.GameBoardUtils.findSafeVertexWithinTargetRound;
import static io.battlesnake.starter.utils.MovementUtils.backTrack;

@Slf4j
public class StrategyFn {
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> eagerFoodCheck = (gameState, optionalTarget) -> {
        log.info("eagerFoodCheck");
        Snake me = gameState.getGameBoard().getMe();
        int[][] myDistanceBoard = gameState.getSnakesDistanceBoardMap().get(me.getHead());
        //boolean canWait = gameState.getGameBoard().getFoodList()
        //                           .parallelStream()
        //                           .anyMatch(food -> me.getHealth() - myDistanceBoard[food.getRow()][food.getColumn()] < 20);
        boolean canWait = me.getHealth()>=60;
        if (!canWait) {
            return StrategyResult.builder().success(true).build();
        }
    
        return STRATEGY_FAILURE;
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> smallSnakeCheck = (gameState, optionalTarget) -> {
        log.info("smallSnakeCheck");
        Snake me = gameState.getGameBoard().getMe();
        if (me.getLength() < 10) {
            return StrategyResult.builder().success(true).build();
        }
        
        return STRATEGY_FAILURE;
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> bigSnakeCheck = (gameState, optionalTarget) -> {
        log.info("middleSnakeCheck");
        Snake me = gameState.getGameBoard().getMe();
        if (me.getLength() >= 20) {
            return StrategyResult.builder().success(true).build();
        }
        
        return STRATEGY_FAILURE;
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> findNearestFoodStrategy = (gameState, optionalPara) -> {
        log.info("findNearestFoodStrategy");
        GameBoard gameBoard = gameState.getGameBoard();
        int[][] distanceBoard = gameState.getSnakesDistanceBoardMap().get(gameBoard.getMe().getHead());
        List<Vertex> foodList = gameBoard.getFoodList();
        
        return findNearestFood(foodList, distanceBoard)
                .map(v -> StrategyResult.builder().success(true).target(v).build())
                .orElse(STRATEGY_FAILURE);
        
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> stealOthersFoodStrategy = (gameState, optionalPara) -> {
        log.info("stealOthersFoodStrategy");
        
        GameBoard gameBoard = gameState.getGameBoard();
        Map<Vertex, int[][]> snakesDistanceBoardMap = gameState.getSnakesDistanceBoardMap();
        // Find the nearest food of each snake.
        Map<Vertex, Optional<Vertex>> snakesNearestFoodMap
                = snakesDistanceBoardMap.entrySet()
                                        .parallelStream()
                                        .collect(Collectors.toMap(entry -> entry.getKey(),
                                                                  entry -> findNearestFood(
                                                                          gameBoard.getFoodList(),
                                                                          entry.getValue())));
        
        // From other snakes nearest food, try to find one closer to me. If cannot find any, return my nearest food.
        Vertex head = gameBoard.getMe().getHead();
        return snakesNearestFoodMap.entrySet()
                            .parallelStream()
                            .filter(entry -> entry.getValue().isPresent()) // the snake has nearest food
                            .filter(entry -> isCloserToMe(snakesDistanceBoardMap, head, entry.getKey(),
                                                          entry.getValue().get())) //the food is closer to me
                            .findFirst().map(entry -> entry.getValue())
                            .orElse(snakesNearestFoodMap.get(head))
                            .map(food -> StrategyResult.builder().success(true).target(food).build())
                            .orElse(STRATEGY_FAILURE);
        
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> chaseTailStrategy = (gameState, optionalPara) -> {
        log.info("chaseTailStrategy");
        GameBoard gameBoard = gameState.getGameBoard();
        int[][] myDistanceBoard = gameState.getSnakesDistanceBoardMap().get(gameBoard.getMe().getHead());
        Vertex tail = gameState.getGameBoard().getMe().getTail();
        
        return findSafeVertexWithinTargetRound(myDistanceBoard, tail)
                .map(v -> StrategyResult.builder().success(true).target(v).build())
                .orElse(STRATEGY_FAILURE);
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> goFurthestStrategy = (gameState, optionalPara) -> {
        log.info("goFurthestStrategy");
        GameBoard gameBoard = gameState.getGameBoard();
        int[][] myDistanceBoard = gameState.getSnakesDistanceBoardMap().get(gameBoard.getMe().getHead());
        return getFarthestVertex(myDistanceBoard)
                .map(v -> StrategyResult.builder().success(true).target(v).build())
                .orElse(STRATEGY_FAILURE);
        
    };
    
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> goEmptyNeighberStrategy = (gameState, optionalPara) -> {
        GameBoard gameBoard = gameState.getGameBoard();
        return findEmptyNeighberVertex(gameBoard.getBoard(), gameBoard.getMe().getHead())
                .map(v -> StrategyResult.builder().success(true).target(v).build())
                .orElse(STRATEGY_FAILURE);
        
    };
    
    //TODO: improve the safe guard
    public static BiFunction<GameState, Optional<Vertex>, StrategyResult> safeGuardStrategy = (gameState, optionalPara) -> {
        log.info("safeGuardStrategy");
        GameBoard gameBoard = gameState.getGameBoard();
        Snake me = gameBoard.getMe();
        int[][] myDistanceBoard = gameState.getSnakesDistanceBoardMap().get(me.getHead());
        Vertex nextPos = backTrack(myDistanceBoard, optionalPara.get());
        // double check next pos
    
        return findAvailableSpaceNearby(gameBoard, nextPos) >= me.getLength() - 1 ?
                StrategyResult.builder().success(true).target(nextPos).build() :
                STRATEGY_FAILURE;
    };
    
    public static Optional<Vertex> findNearestFood(List<Vertex> foodList, int[][] distance) {
        
        Optional<Vertex> ans = Optional.empty();
        if (!foodList.isEmpty()) {
            int min = Integer.MAX_VALUE;
            int dis;
            for (Vertex food : foodList) {
                dis = distance[food.getRow()][food.getColumn()];
                if (dis < min) {
                    min = dis;
                    ans = Optional.of(food);
                }
            }
        }
        return ans;
    }
    
    public static boolean isCloserToMe(Map<Vertex, int[][]> snakesDistanceBoardMap, Vertex me, Vertex other, Vertex food) {
        return getDistance(snakesDistanceBoardMap.get(me), food) < getDistance(snakesDistanceBoardMap.get(other), food);
    }
    
}
