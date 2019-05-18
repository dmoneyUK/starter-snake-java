package io.battlesnake.starter.utils;

import io.battlesnake.starter.model.Vertex;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MovementUtils {
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    public static Vertex backTrack(int[][] myDistanceBoard, Vertex target) {
        int dis = myDistanceBoard[target.getRow()][target.getColumn()];
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
                if (myDistanceBoard[y - dir[0]][x - dir[1]] == dis) {
                    y -= dir[0];
                    x -= dir[1];
                    path.add(Vertex.builder().row(y).column(x).build());
                    break;
                }
            }
        }
        return path.get(path.size() - 1);
    }
    
}
