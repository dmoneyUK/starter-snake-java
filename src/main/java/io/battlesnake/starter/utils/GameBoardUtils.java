package io.battlesnake.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GameBoardUtils {
    
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    
    private static Map<String, int[][]> boardMap = new HashMap<>();
    
    private static Function<JsonNode, int[][]> boardFetchFn = (request) -> boardMap.get(getGameId(request));
    
    private static Function<int[][], int[][]> boardResetFn = (board) -> {
        //TODO: try async for the works
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 1; i < board[0].length - 1; i++) {
            Arrays.fill(board[i], 0);
            board[i][0] = 1;
            board[i][board[0].length - 1] = 1;
        }
        
        return board;
    };
    
    public static int[][] resetGameBoard(JsonNode request) {
        return boardFetchFn.andThen(boardResetFn).apply(request);
    }
    
    public static void createGameBoard(JsonNode request) {
        JsonNode boardNode = request.get("board");
        int[][] gameBoard = new int[boardNode.get("width").asInt() + 2][boardNode.get("height").asInt() + 2];
        boardMap.put(getGameId(request), gameBoard);
    }
    
    public static void removeGameBoard(JsonNode request) {
        boardMap.remove(getGameId(request));
    }
    
    public static List<Vertex> markFood(int[][] board, JsonNode request) throws JsonProcessingException {
    
        Iterator<JsonNode> foodIt = request.findValue("food").elements();
        
        List<Vertex> foodList = new ArrayList<>();
        while (foodIt.hasNext()) {
            Vertex vertex = JSON_MAPPER.treeToValue(foodIt.next(), Vertex.class);
            markOccupied(board, vertex, FOOD);
            foodList.add(vertex);
        }
        return foodList;
    }
    
    public static void markSnakes(int[][] board, JsonNode request) throws JsonProcessingException {
        
        //TODO: try async tasks to mark all snakes
        Iterator<JsonNode> snakeNodes = request.findValue("snakes").elements();
        
        while (snakeNodes.hasNext()) {
            JSON_MAPPER.treeToValue(snakeNodes.next(), Snake.class)
                       .getBody()
                       .parallelStream()
                       .forEach(vertex -> markOccupied(board, vertex, BLOCKED));
        }
    }
    
    private static String getGameId(JsonNode request) {
        return request.get("game").get("id").textValue();
    }
    
    private static Vertex markOccupied(int[][] board, Vertex vertex, int reason) {
        board[vertex.getRow()][vertex.getColumn()] = reason;
        return vertex;
    }
    
}
