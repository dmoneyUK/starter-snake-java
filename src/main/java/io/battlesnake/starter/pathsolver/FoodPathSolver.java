package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.List;

import static io.battlesnake.starter.utils.DistanceBoardUtils.createDistanceBoard;

public class FoodPathSolver implements PathSolver {
    
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    public List<Vertex> findPath(int[][] board, List<Vertex> foodList, Vertex snakeHead) {
        List<Vertex> path;
        int[][] distance = createDistanceBoard(board.length);
        distance[snakeHead.getRow()][snakeHead.getColumn()] = 0;
    
        calculateDistanceDFS(board, snakeHead, distance);
    
        Vertex target = findNearestFood(foodList, distance);
        
        path = backTrack(target, distance);
    
        //PrintingUtils.printBoard(distance);
        //PrintingUtils.printPath(path);
        //PrintingUtils.printVertex(start);
        return path;
    }
    //
    //@Override
    //public Map<String, int[][]> calculateDistanceForAllSnakes(int[][] board, List<int[]> foodList, Map<String, int[]> snakeHeads) {
    //    Map<String, int[][]> snakesDistanceMap = new HashMap<>();
    //    snakeHeads.forEach((name, head) -> {
    //        int[][] distanceBoard = createDistanceBoard(board.length);
    //        calculateDistanceDFS(board, head, distanceBoard);
    //        snakesDistanceMap.put(name, distanceBoard);
    //
    //    });
    //
    //    return snakesDistanceMap;
    //}
    
    @Override
    public String findNextStep(GameBoard gameBoard) {
        Vertex nextPos;
        if (gameBoard.getFoodList().isEmpty()) {
            nextPos = findEmptyNeighbor(gameBoard.getBoard(), gameBoard.getMe());
        } else {
            List<Vertex> path = findPath(gameBoard.getBoard(), gameBoard.getFoodList(), gameBoard.getMe());
            nextPos = path.get(path.size() - 1);
        }
        int y = nextPos.getRow() - gameBoard.getMe().getRow();
        int x = nextPos.getColumn() - gameBoard.getMe().getColumn();
        
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
    
    private Vertex findEmptyNeighbor(int[][] board, Vertex start) {
        for (int[] dir : dirs) {
            int y = start.getRow() + dir[0];
            int x = start.getColumn() + dir[1];
            if (board[y][x] == 0) {
                return Vertex.builder().row(y).column(x).build();
            }
        }
        throw new RuntimeException("Trapped!!!");
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
