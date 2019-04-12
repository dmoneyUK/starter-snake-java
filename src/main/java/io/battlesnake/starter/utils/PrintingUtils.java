package io.battlesnake.starter.utils;

import java.util.List;
import java.util.function.Consumer;

public class PrintingUtils {
    
    private static Consumer<int[][]> boardConsumer = (board) -> {
        System.out.println("____________________________");
        for (int[] row : board) {
            for (int v : row) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
        System.out.println("____________________________");
    };
    
    private static Consumer<List<int[]>> pathConsumer = (row) -> {
        for (int[] v : row) {
            System.out.print("[" + v[0] + "," + v[1] + "], ");
        }
        System.out.println();
    };
    
    private static Consumer<int[]> vertexConsumer = (v) -> {
        System.out.print("[" + v[0] + "," + v[1] + "], ");
        System.out.println();
        
    };
    
    private PrintingUtils() {
    }
    
    public static void printBoard(int[][] board) {
        boardConsumer.accept(board);
    }
    
    public static void printPath(List<int[]> path) {
        pathConsumer.accept(path);
    }
    
    public static void printVertex(int[] vertex) {
        vertexConsumer.accept(vertex);
    }
    
}
