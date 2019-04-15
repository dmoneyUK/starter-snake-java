package io.battlesnake.starter.pathsolver;

import java.util.List;
import java.util.Optional;

public interface PathSolver {
    
    List<int[]> findPath(int[][] board, int[] target, int[] start);
    String findNextStep(int[][] board, Optional<int[]> target, int[] start);
}
