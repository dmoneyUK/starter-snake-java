package io.battlesnake.starter.pathsolver;

import java.util.List;

public interface PathSolver {
    
    List<int[]> findPath(int[][] board, List<int[]> target, int[] start);
    
    String findNextStep(int[][] board, List<int[]> target, int[] start);
}
