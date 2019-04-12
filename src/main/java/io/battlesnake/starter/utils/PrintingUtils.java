package io.battlesnake.starter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class PrintingUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(PrintingUtils.class);
    
    private static Consumer<int[][]> boardConsumer = (board) -> {
        if(board==null){
            return;
        }
        LOG.info("__________ Board Begin __________________");
        for (int[] row : board) {
            String s = "";
            for (int v : row) {
                s += " " + v;
            }
            LOG.info(s);
        }
        LOG.info("__________Board End __________________");
    };
    
    private static Consumer<List<int[]>> pathConsumer = (row) -> {
        if(row==null){
            return;
        }
        for (int[] v : row) {
            LOG.info("Path: [" + v[0] + "," + v[1] + "], ");
        }
    };
    
    private static Consumer<int[]> vertexConsumer = (v) -> {
        if(v==null){
            return;
        }
        LOG.info("Vertex: [" + v[0] + "," + v[1] + "], ");
        
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
