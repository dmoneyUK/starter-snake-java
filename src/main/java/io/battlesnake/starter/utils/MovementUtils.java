package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.battlesnake.starter.utils.DistanceBoardUtils.getAllSnakesDistanceBoards;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getDistance;
import static io.battlesnake.starter.utils.GameBoardUtils.findEmptyNeighberVertex;
import static io.battlesnake.starter.utils.GameBoardUtils.hasFoodOnGameBoard;

public class MovementUtils {
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    // calculate next movement direction based on the next location to move to and current location.
    public static String findNextMovement(GameBoard gameBoard, Vertex me) {
        
        Vertex nextPos = findNextPosition(gameBoard, me);
        
        int y = nextPos.getRow() - me.getRow();
        int x = nextPos.getColumn() - me.getColumn();
        
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
    
    // Find the location (a vertex) to move to.
    private static Vertex findNextPosition(GameBoard gameBoard, Vertex me) {
        Vertex nextPos = null;
        
        // Calculate the distance board for all snakes
        Map<Vertex, int[][]> snakesDistanceMap = getAllSnakesDistanceBoards(gameBoard);
        int[][] myDistanceBoard = snakesDistanceMap.get(me);
        
        //Try to find the food which is closer to me than others. (may not be the closest to me).
        Optional<Vertex> optionalTarget = Optional.empty();
        if (hasFoodOnGameBoard(gameBoard)) {
            optionalTarget = findFoodCloserToMeThanOthers(gameBoard.getFoodList(), snakesDistanceMap, me);
        }
        
        // Move to some safe place, when:
        // a) Food has not be generated in the round when it is eaten,
        // b) No access to any food
        if (optionalTarget.isPresent()) {
            nextPos = backTrackNextPosition(myDistanceBoard, optionalTarget.get());
        } else {
            optionalTarget = DistanceBoardUtils.getFarthestVertex(myDistanceBoard);
            if (optionalTarget.isPresent()) {
                nextPos = backTrackNextPosition(myDistanceBoard, optionalTarget.get());
            } else {
                nextPos = findEmptyNeighberVertex(gameBoard.getBoard(), me);
            }
        }
        
        return nextPos;
    }
    
    private static Vertex backTrackNextPosition(int[][] myDistanceBoard, Vertex target) {
        List<Vertex> path = backTrackPath(target, myDistanceBoard);
        return path.get(path.size() - 1);
    }
    
    private static List<Vertex> backTrackPath(Vertex target, int[][] distance) {
        int dis = distance[target.getRow()][target.getColumn()];
        if (dis == Integer.MAX_VALUE) {
            return null;
        }
        int y = target.getRow();
        int x = target.getColumn();
        List<Vertex> path = new ArrayList<>();
        path.add(Vertex.builder().row(y).column(x).build());
        while (dis > 1) {
            dis--;
            for (int[] dir : dirs) {
                if (distance[y - dir[0]][x - dir[1]] == dis) {
                    y -= dir[0];
                    x -= dir[1];
                    path.add(Vertex.builder().row(y).column(x).build());
                    break;
                }
            }
        }
        return path;
    }
    
    /* ########################## Greedy Strategy ########################## */
    private static Optional<Vertex> findFoodCloserToMeThanOthers(List<Vertex> foodList, Map<Vertex, int[][]> snakesDistanceBoard,
                                                                 Vertex me) {
        
        // Find the nearest food of each snake.
        Map<Vertex, Optional<Vertex>> snakesNearestFoodMap = snakesDistanceBoard
                .entrySet()
                .parallelStream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> findNearestFood(foodList, entry.getValue())));
        
        Optional<Vertex> ans = snakesNearestFoodMap.get(me);
        // From other snakes nearest food, try to find one closer to me. If cannot find any, return my nearest food.
        ans = snakesNearestFoodMap.entrySet()
                                  .parallelStream()
                                  .filter(entry -> entry.getValue().isPresent()) // the snake has nearest food
                                  .filter(entry -> isCloserToMe(snakesDistanceBoard, me, entry.getKey(),
                                                                entry.getValue().get())) //the food is closer to me
                                  .findFirst()
                                  .map(entry -> entry.getValue())
                                  .orElse(ans);
        return ans;
    }
    
    private static Optional<Vertex> findNearestFood(List<Vertex> foodList, int[][] distance) {
        
        Optional<Vertex> ans = Optional.empty();
        int min = Integer.MAX_VALUE;
        int dis;
        for (Vertex food : foodList) {
            dis = distance[food.getRow()][food.getColumn()];
            if (dis < min) {
                min = dis;
                ans = Optional.of(food);
            }
        }
        return ans;
    }
    
    private static boolean isCloserToMe(Map<Vertex, int[][]> snakesDistanceBoardMap, Vertex me, Vertex other, Vertex food) {
        return getDistance(snakesDistanceBoardMap.get(me), food) < getDistance(snakesDistanceBoardMap.get(other), food);
    }
    /* ######################################################################### */
}
