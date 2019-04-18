package io.battlesnake.starter.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GameBoardUtils {
    
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    
    
    private static Map<String, int[][]> boardMap = new HashMap<>();
    
    private static Function<JsonNode, int[][]> boardFetchFn = (request) -> boardMap.get(getGameId(request));
    
    private static Function<int[][], int[][]> boardResetFn = (board) -> {
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 1; i < board[0].length - 1; i++) {
            Arrays.fill(board[i], 0);
            board[i][0] = 1;
            board[i][board[0].length - 1] = 1;
        }
        
        return board;
    };
    
    public static int[] getVertex(JsonNode node) {
        return new int[]{node.findValue("y").asInt() + 1, node.findValue("x").asInt() + 1};
    }
    
    public static int[][] resetGameBoard(JsonNode request) {
        return boardFetchFn.andThen(boardResetFn).apply(request);
    }
    
    public static void createGameBoard(JsonNode request) {
        JsonNode boardNode = request.get("board");
        int[][] gameBoard = new int[boardNode.get("width").asInt() + 2][boardNode.get(
                "height").asInt() + 2];
        boardMap.put(getGameId(request), gameBoard);
    }
    
    public static List<int[]> markFood(int[][] board, JsonNode request) {
        List<int[]> allFood = new ArrayList<>();
        List<JsonNode> foodNodes = request.findValues("food");
        for (JsonNode node : foodNodes) {
            int[] foodVertex = getVertex(node);
            markOccupied(board, foodVertex, FOOD);
            allFood.add(foodVertex);
        }
        return allFood;
    }
    
    public static void markSankes(int[][] board, List<JsonNode> snakes) {
        snakes.parallelStream().forEach(body -> {
            for (int i = body.size() - 1; i >= 0; i--) {
                JsonNode node = body.get(i);
                int[] vertex = getVertex(node);
                markOccupied(board, vertex, BLOCKED);
            }
        });
    }
    
    private static String getGameId(JsonNode request) {
        return request.get("game").get("id").textValue();
    }
    
    private static void markOccupied(int[][] board, int[] vertex, int reason) {
        board[vertex[0]][vertex[1]] = reason;
    }
    
}
