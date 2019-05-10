package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.DistanceBoard;
import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class DistanceBoardUtils {
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    public static int[][] createDistanceBoard(int length) {
    
        int[][] distance = new int[length][length];
        for (int[] row : distance)
            Arrays.fill(row, Integer.MAX_VALUE);
        DistanceBoard.builder().distance(distance).build();
        return distance;
    }
    
    public static int getDistance(int[][] distanceBoard, Vertex target) {
        return distanceBoard[target.getRow()][target.getColumn()];
    }
    
    public static Optional<Vertex> getFarthestVertex(int[][] distanceBoard) {
        int max = 0;
        Optional<Vertex> optionalFarthest = Optional.empty();
        int maxRow = -1;
        int maxColumn = -1;
        for (int row = 0; row < distanceBoard.length; row++) {
            for (int column = 0; column < distanceBoard.length; column++) {
                int distance = distanceBoard[row][column];
                if (distance != Integer.MAX_VALUE && max < distance) {
                    max = distance;
                    maxRow = row;
                    maxColumn = column;
                }
            }
        }
        if (maxColumn != -1) {
            optionalFarthest = Optional.of(Vertex.builder().row(maxRow).column(maxColumn).build());
        }
        return optionalFarthest;
        
    }
    
    public static Map<Vertex, int[][]> getAllSnakesDistanceBoards(GameBoard gameBoard) {
        
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
    
    // Get the distance board for me. This takes consideration of the dangerous areas on the game board.
    private static int[][] getMyDistanceBoard(GameBoard gameBoard) {
       
        int[][] boardClone = GameBoardUtils.getBoardClone(gameBoard);
    
        //PrintingUtils.printBoard(boardClone);
    
        Snake me = gameBoard.getMe();
        // If not grow in the next turn, current tail is safe.
        if (!me.getTail().equals(me.getBody().get(me.getLength() - 2))) {
            boardClone[me.getTail().getRow()][me.getTail().getColumn()] = 0;
        }
        if (me.getHealth() > 20) {
            GameBoardUtils.findDangerous(gameBoard)
                          .parallelStream()
                          .forEach(dangerous -> GameBoardUtils.markDangerous(boardClone, dangerous));
        }
        PrintingUtils.printBoard(boardClone);
        return calculateDistanceBoard(boardClone, me.getHead());
    }
    
    // DFS search to calculate the distance from the snake's head to each vertex on the game board.
    private static int[][] calculateDistanceBoard(int[][] board, Vertex snakeHead) {
        int[][] distance = createDistanceBoard(board.length);
        
        distance[snakeHead.getRow()][snakeHead.getColumn()] = 0;
        
        calculateDistanceDFS(board, snakeHead, distance);
        
        return distance;
    }
    
    private static void calculateDistanceDFS(int[][] board, Vertex start, int[][] distance) {
        
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
    
}
