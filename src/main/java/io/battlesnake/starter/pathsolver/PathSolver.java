package io.battlesnake.starter.pathsolver;

import java.util.List;

public interface PathSolver {
    
    List<int[]> findPath(int[][] board, int[] target, int[] start);
    String findNextStep(int[][] board, int[] target, int[] start);
}
