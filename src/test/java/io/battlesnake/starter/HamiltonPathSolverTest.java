package io.battlesnake.starter;

import io.battlesnake.starter.pathsolver.HamiltonPathSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HamiltonPathSolverTest {
    
    private HamiltonPathSolver testObj;
    private static int SIZE = 4;
    private int[][] GRAPH_WITH_HC = {{0, 1, 0, 0},
                                     {1, 0, 1, 0},
                                     {0, 1, 0, 1},
                                     {1, 0, 1, 0}};
    
    @Mock private int[][] graphMock;
    
    @BeforeEach
    public void setUp() {
        testObj = new HamiltonPathSolver(SIZE);
    }
    
    @Test
    public void shouldInitializePath() {
        testObj.resetPath();
        int[] path = testObj.getPath();
        assertThat(path[0]).isEqualTo(0);
        for (int i = 1; i < path.length; i++) {
            assertThat(path[i]).isEqualTo(-1);
        }
        
    }
    
    @Test
    public void shouldFindNoHamiltonCycle() {
        boolean found = testObj.findPath(graphMock);
        assertThat(found).isFalse();
    }
    
    @Test
    public void shouldReturnTrueWhenArrivingLastNodeAndHaveEdgeToTheFirst() {
        
        int[] path = {3, 0, 1, 2};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.hamiltonCircleSearch(GRAPH_WITH_HC, path, 3);
        assertThat(actual).isTrue();
    }
    
    @Test
    public void shouldReturnFalseWhenArrivingLastNodeButNoEdgeToTheFirst() {
        int[] path = {2, 0, 1, 2};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.hamiltonCircleSearch(GRAPH_WITH_HC, path, 3);
        assertThat(actual).isFalse();
    }
    
    
    @Test
    public void shouldReturnTrueWhenSafe() {
        int[] path = {2, 0, 1, 2};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.hamiltonCircleSearch(GRAPH_WITH_HC, path, 3);
        assertThat(actual).isTrue();
    }
    
    
    @Test
    public void shouldReturnFalseWhenNoEdgeFromPosToVertex() {
        int[] path = {-1, -1, -1, -1};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.isSafe(GRAPH_WITH_HC, path, 1, 3);
        assertThat(actual).isFalse();
    }
    
    @Test
    public void shouldReturnFalseWhenHaveEdgeFromPosToVertexButAlreadyAdded() {
        int[] path = {0, 2, 3, -1};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.isSafe(GRAPH_WITH_HC, path, 3, 2);
        assertThat(actual).isFalse();
    }
    
    @Test
    public void shouldReturnTrueWhenHaveEdgeFromPosToVertexAndVertexIsNotAdded() {
        int[] path = {0, -1, -1, -1};
        testObj = new HamiltonPathSolver(4);
        boolean actual = testObj.isSafe(GRAPH_WITH_HC, path, 3, 2);
        assertThat(actual).isTrue();
    }
    
}
