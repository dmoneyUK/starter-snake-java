package io.battlesnake.starter.pathsolver;

import java.util.Arrays;

public class HamiltonPathSolver {
    
    private int size;
    private int[] path;
    
    public HamiltonPathSolver(int size) {
        this.size = size;
    }
    
    public boolean findPath(int[][] graph) {
        resetPath();
        return false;
    }
    
    public boolean hamiltonCircleSearch(int[][] graph, int[] path, int pos) {
        //Check the finishing step
        if (pos == size - 1) {
            if (graph[path[pos]][path[0]] == 1) {
                return true;
            }
            return false;
        }
        
        for (int v = 1; v < size; v++) {
            if (isSafe(graph, path, 1, v)) {
                path[pos] = v;
                if (hamiltonCircleSearch(graph, path, pos++)) {
                    return true;
                }
                path[pos] = -1;
            }
        }
        return true;
    }
    
    public boolean isSafe(int[][] graph, int[] path, int pos, int vertex) {
        if (graph[pos][vertex] == 0) {
            return false;
        }
        
        for (int i = 0; i < pos; i++) {
            if (path[i] == vertex) {
                return false;
            }
        }
        
        return true;
    }
    
    public void resetPath() {
        path = new int[size];
        Arrays.fill(path, -1);
        path[0] = 0;
    }
    
    public int[] getPath() {
        return path;
    }
    
}
