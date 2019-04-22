package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.battlesnake.starter.utils.DistanceBoardUtils.createDistanceBoard;
import static io.battlesnake.starter.utils.DistanceBoardUtils.getDistance;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class FoodPathSolver implements PathSolver {
    
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    @Override
    public String findNextStep(GameBoard gameBoard) {
        Vertex me = gameBoard.getMe().getHead();
        checkHeadToHeadSafe(gameBoard);
        Vertex nextPos = findNextVertex(gameBoard, me);
    
        return findNextMovment(me, nextPos);
    }
    
    private boolean checkHeadToHeadSafe(GameBoard gameBoard) {
        Snake me = gameBoard.getMe();
        gameBoard.getSnakes()
                 .parallelStream()
                 .map(snake -> {
                     return false;
                     //return getHeadToHeadDistance(me, snake.getBody().get(0)) == 1 && snake.getBody().size() < me.getBody().size());
                 });
        return false;
    }
    
    private int getHeadToHeadDistance(Vertex me, Vertex head) {
        return Math.abs(head.getRow() - me.getRow()) + Math.abs(head.getRow() - me.getRow());
    }
    
    // Find the location (a vertex) to move to.
    private Vertex findNextVertex(GameBoard gameBoard, Vertex me) {
        Vertex nextPos;
        if (noFoodOnGameBoard(gameBoard)) {
            // Food has not be generated in the round when it is eaten, move to any safe place.
            nextPos = findEmptyNeighberVertex(gameBoard.getBoard(), me);
        } else {
            // Calculate the distance board for all snakes and then try to find the food
            // which is closer to me than others. ( may not be the closest to me).
            Map<Vertex, int[][]> snakesDistanceMap = getAllSnakesDistanceBoards(gameBoard);
            int[][] myDistanceBoard = snakesDistanceMap.get(me);
            Vertex target = findFoodCloserToMeThanOthers(gameBoard.getFoodList(), snakesDistanceMap, me);
            
            // back track to draw the path to the food closer to me and take the first step in the path.
            List<Vertex> path = backTrack(target, myDistanceBoard);
            nextPos = path.get(path.size() - 1);
        }
        return nextPos;
    }
    
    // calculate next movement direction based on the next location to move to and current location.
    private String findNextMovment(Vertex me, Vertex nextPos) {
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
        return gameBoard.getSnakes()
                        .parallelStream()
                        .map(Snake::getHead)
                        .collect(toMap(identity(), head -> calculateDistanceBoard(gameBoard.getBoard(), head)));
    }
    
    private boolean noFoodOnGameBoard(GameBoard gameBoard) {
        return gameBoard.getFoodList().isEmpty();
    }
    
    // DFS search to calculate the distance from the snake's head to each vertex on the game board.
    private int[][] calculateDistanceBoard(int[][] board, Vertex snakeHead) {
        int[][] distance = createDistanceBoard(board.length);
        
        distance[snakeHead.getRow()][snakeHead.getColumn()] = 0;
        
        calculateDistanceDFS(board, snakeHead, distance);
        
        return distance;
    }
    
    private void calculateDistanceDFS(int[][] board, Vertex start, int[][] distance) {
        
        for (int[] dir : dirs) {
            int row = start.getRow() + dir[0];
            int column = start.getColumn() + dir[1];
            int count = 0;
    
            if (board[row][column] != 1) {
                row += dir[0];
                column += dir[1];
                count++;
            }
            if (distance[row - dir[0]][column - dir[1]] > distance[start.getRow()][start.getColumn()] + count) {
                distance[row - dir[0]][column - dir[1]] = distance[start.getRow()][start.getColumn()] + count;
                calculateDistanceDFS(board, Vertex.builder().row(row - dir[0]).column(column - dir[1]).build(), distance);
            }
        }
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
    
    private Vertex findFoodCloserToMeThanOthers(List<Vertex> foodList, Map<Vertex, int[][]> snakesDistanceBoard, Vertex me) {
        
        // Find the nearest food of each snake.
        Map<Vertex, Vertex> snakesNearestFood = snakesDistanceBoard
                .entrySet()
                .parallelStream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> findNearestFood(foodList, entry.getValue())));
        
        // From other snakes nearest food, try to find one closer to me. If cannot find any, return my nearest food.
        return snakesNearestFood.entrySet()
                                .stream()
                                .filter(entry -> getDistance(snakesDistanceBoard.get(me), entry.getValue()) < getDistance(
                                        snakesDistanceBoard.get(entry.getKey()), entry.getValue()))
                                .findFirst()
                                .map(entry -> entry.getValue())
                                .orElse(snakesNearestFood.get(me));
    }
    
    private Vertex findNearestFood(List<Vertex> foodList, int[][] distance) {
        
        Vertex ans = null;
        int min = Integer.MAX_VALUE;
        int dis;
        for (Vertex food : foodList) {
            dis = distance[food.getRow()][food.getColumn()];
            if (dis < min) {
                min = dis;
                ans = food;
            }
        }
        return ans;
    }
    
    private List<Vertex> backTrack(Vertex target, int[][] distance) {
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
}
