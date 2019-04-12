package io.battlesnake.starter.pathsolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FoodPathSolverTest {
    
    private FoodPathSolver testObj;
    
    @BeforeEach
    public void setUp() {
        testObj = new FoodPathSolver();
    }
    
    @Test
    void findPath() {
        int[][] board = {{1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 0, 1}, {1, 0, 0, 0, 0, 1}, {1, 0, 0, 0, 2, 1}, {1, 0, 0, 0, 0, 1},
                         {1, 1, 1, 1, 1, 1}};
        int[] target = {4, 4};
        int[] start = {1, 3};
        List<int[]> actual = testObj.findPath(board, target, start);
        int[][] expected = {{4, 0}, {4, 1}, {4, 2},{4,3}};
        assertThat(actual).isEqualTo(Arrays.asList(expected));
    }
}
