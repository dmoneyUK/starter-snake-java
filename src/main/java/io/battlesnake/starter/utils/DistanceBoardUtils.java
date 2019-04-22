package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.Vertex;

import java.util.Arrays;

public class DistanceBoardUtils {
    
    public static int[][] createDistanceBoard(int length) {
        int[][] distance = new int[length][length];
        for (int[] row : distance)
            Arrays.fill(row, Integer.MAX_VALUE);
        return distance;
    }
    
    public static int getDistance(int[][] distanceBoard, Vertex target) {
        return distanceBoard[target.getRow()][target.getColumn()];
    }
    
}