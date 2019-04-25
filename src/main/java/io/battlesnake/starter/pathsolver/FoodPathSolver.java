package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;
import io.battlesnake.starter.utils.DistanceBoardUtils;
import io.battlesnake.starter.utils.GameBoardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.battlesnake.starter.utils.DistanceBoardUtils.createDistanceBoard;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getDistance;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class FoodPathSolver implements PathSolver {
    
    public static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    @Override
    public String findNextStep(GameBoard gameBoard) {
    
        Vertex me = gameBoard.getMe().getHead();
    
        Vertex nextPos = findNextPosition(gameBoard, me);
    
        return findNextMovement(me, nextPos);
    }
    
    // Find the location (a vertex) to move to.
    private Vertex findNextPosition(GameBoard gameBoard, Vertex me) {
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
        if(optionalTarget.isPresent()){
            nextPos = backTrackNextPosition(myDistanceBoard, optionalTarget.get());
        }else{
            optionalTarget = DistanceBoardUtils.getFarthestVertex(myDistanceBoard);
            if(optionalTarget.isPresent()){
                nextPos = backTrackNextPosition(myDistanceBoard, optionalTarget.get());
            }else{
                nextPos = findEmptyNeighberVertex(gameBoard.getBoard(), me);
            }
        }
        
        //if (optionalTarget.isPresent()) {
        //    // back track to draw the path to the food closer to me and take the first step in the path.
        //    nextPos = backTrackNextPosition(myDistanceBoard, optionalTarget.get());
        //}else if(DistanceBoardUtils.getFarthestVertex(myDistanceBoard)){
        //    /* Random movement */
        //    //return nextPos.orElseGet(() -> findEmptyNeighberVertex(gameBoard.getBoard(), me));
        //
        //    /* Move to the farthest*/
        //    nextPos = backTrackNextPosition(myDistanceBoard, DistanceBoardUtils.getFarthestVertex(myDistanceBoard));
        //}
        //
        return nextPos;
    }
    
    private Vertex backTrackNextPosition(int[][] myDistanceBoard, Vertex target) {
        List<Vertex> path = backTrackPath(target, myDistanceBoard);
        return path.get(path.size() - 1);
    }
    
    // calculate next movement direction based on the next location to move to and current location.
    private String findNextMovement(Vertex me, Vertex nextPos) {
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
    
    private Map<Vertex, int[][]> getAllSnakesDistanceBoards(GameBoard gameBoard) {
    
        int[][] myDistanceBoard = getMyDistanceBoard(gameBoard);
    
        Snake me = gameBoard.getMe();
        Map<Vertex, int[][]> allSnakesDistanceBoards = gameBoard.getSnakes()
                                                                .parallelStream()
                                                                .filter(snake -> !snake.equals(me))
                                                                .map(Snake::getHead)
                                                                .collect(toMap(identity(),
                                                                               head -> calculateDistanceBoard(gameBoard.getBoard(), head)));
    
        allSnakesDistanceBoards.put(me.getHead(), myDistanceBoard);
        return allSnakesDistanceBoards;
    
    }
    
    //
    private int[][] getMyDistanceBoard(GameBoard gameBoard) {
        
        int[][] boardClone = GameBoardUtils.getBoardClone(gameBoard);
        
        GameBoardUtils.findDangerous(gameBoard)
                      .parallelStream()
                      .forEach(dangerous -> GameBoardUtils.markDangerous(boardClone, dangerous));
        return calculateDistanceBoard(boardClone, gameBoard.getMe().getHead());
    }
    
    private boolean hasFoodOnGameBoard(GameBoard gameBoard) {
        return !gameBoard.getFoodList().isEmpty();
    }
    
    // DFS search to calculate the distance from the snake's head to each vertex on the game board.
    private int[][] calculateDistanceBoard(int[][] board, Vertex snakeHead) {
        int[][] distance = createDistanceBoard(board.length);
        
        distance[snakeHead.getRow()][snakeHead.getColumn()] = 0;
    
        calculateDistanceDFS(board, snakeHead, distance);
    
        return distance;
    }
    
    private void calculateDistanceDFS(int[][] board, Vertex start, int[][] distance) {
    
        int startDistance = distance[start.getRow()][start.getColumn()];
        
        for (int[] dir : dirs) {
            int row = start.getRow() + dir[0];
            int column = start.getColumn() + dir[1];
    
            if (board[row][column] != 1) {
    
                int nextDistanceInOldRoute = distance[row][column];
                int nextDistanceInNewRoute = startDistance + 1;
    
                if (nextDistanceInOldRoute > nextDistanceInNewRoute) {
                    distance[row][column] = nextDistanceInNewRoute;
                    calculateDistanceDFS(board, Vertex.builder().row(row).column(column).build(), distance);
                }
            }
        }
    }
    
    /* ########################## Greed Strategy ########################## */
    private Optional<Vertex> findFoodCloserToMeThanOthers(List<Vertex> foodList, Map<Vertex, int[][]> snakesDistanceBoard, Vertex me) {
        
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
    
    private Optional<Vertex> findNearestFood(List<Vertex> foodList, int[][] distance) {
        
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
    
    private boolean isCloserToMe(Map<Vertex, int[][]> snakesDistanceBoardMap, Vertex me, Vertex other, Vertex food) {
        return getDistance(snakesDistanceBoardMap.get(me), food) < getDistance(snakesDistanceBoardMap.get(other), food);
    }
    
    private List<Vertex> backTrackPath(Vertex target, int[][] distance) {
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
    
    private Vertex findEmptyNeighberVertex(int[][] board, Vertex start) {
        for (int[] dir : dirs) {
            int y = start.getRow() + dir[0];
            int x = start.getColumn() + dir[1];
            if (board[y][x] == 0) {
                return Vertex.builder().row(y).column(x).build();
            }
        }
        throw new RuntimeException("Trapped!!!");
    }
    
    /* ######################################################################### */
    
}
