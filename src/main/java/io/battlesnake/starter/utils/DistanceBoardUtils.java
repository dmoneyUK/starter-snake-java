package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.DistanceBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.Arrays;
import java.util.Optional;

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
    
}
