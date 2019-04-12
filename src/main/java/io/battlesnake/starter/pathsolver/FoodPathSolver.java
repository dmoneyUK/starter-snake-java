package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.utils.PrintingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodPathSolver implements PathSolver {
    
    private List<int[]> path;
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    @Override
    public List<int[]> findPath(int[][] board, int[] target, int[] start) {
        path = new ArrayList<>();
        int[][] distance = new int[board.length][board[0].length];
        for (int[] row : distance)
            Arrays.fill(row, Integer.MAX_VALUE);
        distance[start[0]][start[1]] = 0;
        findPathDfs(board, target, start, distance);
        path = backTrack(target, distance);
        //PrintingUtils.printBoard(distance);
        //PrintingUtils.printPath(path);
        //PrintingUtils.printVertex(start);
        return path;
    }
    
    @Override
    public String findNextStep(int[][] board, int[] target, int[] start) {
        int[] nextPos = findPath(board, target, start).get(path.size() - 1);
        int y = nextPos[0] - start[0];
        int x = nextPos[1] - start[1];
        //PrintingUtils.printVertex(new int[]{y,x});
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
    
    private void findPathDfs(int[][] board, int[] target, int[] start, int[][] distance) {
        
        for (int[] dir : dirs) {
            int y = start[0] + dir[0];
            int x = start[1] + dir[1];
            int count = 0;
            //while (board[y][x] == 0) {
            //    y += dir[0];
            //    x += dir[1];
            //    count++;
            //}
            if (board[y][x] != 1) {
                y += dir[0];
                x += dir[1];
                count++;
            }
            if (distance[y - dir[0]][x - dir[1]] > distance[start[0]][start[1]] + count) {
                distance[y - dir[0]][x - dir[1]] = distance[start[0]][start[1]] + count;
                findPathDfs(board, target, new int[]{y - dir[0], x - dir[1]}, distance);
            }
        }
    }
    
    private List<int[]> backTrack(int[] target, int[][] distance) {
        int dis = distance[target[0]][target[1]];
        if (dis == Integer.MAX_VALUE) {
            return null;
        }
        int y = target[0];
        int x = target[1];
        List<int[]> path = new ArrayList<>();
        path.add(new int[]{y, x});
        while (dis > 1) {
            dis--;
            for (int[] dir : dirs) {
                if (distance[y - dir[0]][x - dir[1]] == dis) {
                    y -= dir[0];
                    x -= dir[1];
                    path.add(new int[]{y, x});
                    break;
                }
            }
        }
        return path;
    }
}
