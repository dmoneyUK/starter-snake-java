package io.battlesnake.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class GameBoardUtils {
    
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    public static GameBoard initGameBoard(JsonNode request) {
        
        return createGameBoardFn
                .andThen(markBordersFn)
                .andThen(markSnakesFn)
                .andThen(markFoodFn)
                .apply(request);
    }
    
    public static Snake findMe(JsonNode request) throws JsonProcessingException {
        return JSON_MAPPER.treeToValue(request.get("you"), Snake.class);
    }
    
    private static Function<JsonNode, GameBoard> createGameBoardFn = (request) -> {
        JsonNode boardNode = request.get("board");
        GameBoard gameBoard = null;
        try {
            gameBoard = GameBoard.builder()
                                 .board(new int[boardNode.get("width").asInt() + 2][boardNode.get("height").asInt() + 2])
                                 .foodList(getFoodList(request))
                                 .snakes(findSnakes(request))
                                 .me(findMe(request))
                                 .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return gameBoard;
    };
    
    private static Function<GameBoard, GameBoard> markBordersFn = (gameBoard) -> {
        //TODO: try async for the works
        int[][] board = gameBoard.getBoard();
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 1; i < board[0].length - 1; i++) {
            Arrays.fill(board[i], 0);
            board[i][0] = 1;
            board[i][board[0].length - 1] = 1;
        }
        
        return gameBoard;
    };
    
    private static Function<GameBoard, GameBoard> markSnakesFn = gameBoard -> {
        gameBoard.getSnakes()
                 .parallelStream()
                 .forEach(snake -> snake.getBody()
                                        .parallelStream()
                                        .forEach((Vertex vertex) -> markOccupied(gameBoard.getBoard(), vertex, BLOCKED)));
        return gameBoard;
    };
    
    private static Function<GameBoard, GameBoard> markFoodFn = gameBoard -> {
        gameBoard.getFoodList()
                 .parallelStream()
                 .forEach(food -> markOccupied(gameBoard.getBoard(), food, FOOD));
        return gameBoard;
    };
    
    private static List<Vertex> getFoodList(JsonNode request) throws JsonProcessingException {
        
        List<Vertex> foodList = new ArrayList<>();
        Iterator<JsonNode> foodIt = request.findValue("food").elements();
        
        while (foodIt.hasNext()) {
            Vertex vertex = JSON_MAPPER.treeToValue(foodIt.next(), Vertex.class);
            foodList.add(vertex);
        }
        return foodList;
    }
    
    private static List<Snake> findSnakes(JsonNode request) throws JsonProcessingException {
        
        List<Snake> snakes = new ArrayList<>();
        Iterator<JsonNode> snakeIt = request.findValue("snakes").elements();
        
        while (snakeIt.hasNext()) {
            snakes.add(JSON_MAPPER.treeToValue(snakeIt.next(), Snake.class));
        }
        return snakes;
    }
    
    private static Vertex markOccupied(int[][] board, Vertex vertex, int reason) {
        board[vertex.getRow()][vertex.getColumn()] = reason;
        return vertex;
    }
    
}
