package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.DistanceBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.Arrays;

public class DistanceBoardUtils {
    
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
    
    public static Vertex getFarthestVertex(int[][] distanceBoard) {
        int max = 0;
        int maxRow = 0;
        int maxColumn = 0;
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
        return Vertex.builder().row(maxRow).column(maxColumn).build();
    }
    
}
