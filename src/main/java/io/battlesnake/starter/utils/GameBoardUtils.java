package io.battlesnake.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class GameBoardUtils {
    
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    private static Function<JsonNode, GameBoard> createGameBoardFn = (request) -> {
        JsonNode boardNode = request.get("board");
        GameBoard gameBoard = null;
        try {
            gameBoard = GameBoard.builder()
                                 .board(new int[boardNode.get("width").asInt() + 2][boardNode.get("height").asInt() + 2])
                                 .foodList(findAllFood(request))
                                 .snakes(findSnakes(request))
                                 .me(findMe(request))
                                 .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return gameBoard;
    };
    
    public static GameBoard initGameBoard(JsonNode request) {
        
        // try async for each task
        return createGameBoardFn
                .andThen(GameBoardUtils::markBorders)
                .andThen(GameBoardUtils::markSnakes)
                .andThen(GameBoardUtils::markFood)
                .apply(request);
    }
    
    public static Snake findMe(JsonNode request) throws JsonProcessingException {
        return JSON_MAPPER.treeToValue(request.get("you"), Snake.class);
    }
    
    public static List<Vertex> findDangerous(GameBoard gameBoard) {
        List<Vertex> result =  new ArrayList<>();
        if(gameBoard.getMe().getHealth()>20) {
            result.addAll(findSelfCollisionRiskFn(gameBoard));
        }
        result.addAll(findHeadToHeadRiskFn(gameBoard));
        return result;
        
    }
    
    public static int[][] getBoardClone(GameBoard gameBoard) {
        
        return Arrays.stream(gameBoard.getBoard())
                     .map(r -> r.clone())
                     .toArray(int[][]::new);
    }
    
    public static void markDangerous(int[][] board, Vertex dangerous) {
        board[dangerous.getRow()][dangerous.getColumn()] = 1;
    }
    
    public static boolean hasFoodOnGameBoard(GameBoard gameBoard) {
        return !gameBoard.getFoodList().isEmpty();
    }
    
    public static Vertex findEmptyNeighberVertex(int[][] board, Vertex start) {
        for (int[] dir : dirs) {
            int y = start.getRow() + dir[0];
            int x = start.getColumn() + dir[1];
            if (board[y][x] == 0) {
                return Vertex.builder().row(y).column(x).build();
            }
        }
        throw new RuntimeException("Trapped!!!");
    }
    
    
    private static GameBoard markBorders(GameBoard gameBoard) {
        int[][] board = gameBoard.getBoard();
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 1; i < board[0].length - 1; i++) {
            Arrays.fill(board[i], 0);
            board[i][0] = 1;
            board[i][board[0].length - 1] = 1;
        }
        return gameBoard;
    }
    
    private static GameBoard markSnakes(GameBoard gameBoard) {
        
        gameBoard.getSnakes()
                 .parallelStream()
                 .forEach(snake -> markSnake(gameBoard, snake));
        return gameBoard;
    }
    
    private static Snake markSnake(GameBoard gameBoard, Snake snake) {
        snake.getBody()
             .parallelStream()
             .forEach(vertex -> markOccupied(gameBoard.getBoard(), vertex, BLOCKED));
        return snake;
    }
    
    private static GameBoard markFood(GameBoard gameBoard) {
    
        gameBoard.getFoodList()
                 .parallelStream()
                 .filter(food -> gameBoard.getBoard()[food.getRow()][food.getColumn()] != 1)
                 .forEach(food -> markOccupied(gameBoard.getBoard(), food, FOOD));
        return gameBoard;
    }
    
    private static Vertex markOccupied(int[][] board, Vertex vertex, int reason) {
        board[vertex.getRow()][vertex.getColumn()] = reason;
        return vertex;
    }
    
    private static List<Vertex> findAllFood(JsonNode request) throws JsonProcessingException {
        
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
    
    
    private static int getHeadToHeadDistance(Vertex me, Vertex head) {
        return Math.abs(head.getRow() - me.getRow()) + Math.abs(head.getColumn() - me.getColumn());
    }
    
    private static List<Vertex> findHeadToHeadRiskFn(GameBoard gameBoard) {
        Snake me = gameBoard.getMe();
        return gameBoard.getSnakes()
                        .parallelStream()
                        .filter(snake -> getHeadToHeadDistance(me.getHead(), snake.getHead()) == 2)
                        .filter(snake -> !snake.isShortThan(me))
                        .map(Snake::getMovementRange)
                        .flatMap(moveRange -> moveRange.stream())
                        .collect(toList());
    }
    
    private static List<Vertex> findSelfCollisionRiskFn(GameBoard gameBoard) {
        Snake me = gameBoard.getMe();
        Vertex head = me.getHead();
        
        Set<Vertex> risks = new HashSet<>();
        
        int headRow = head.getRow();
        int headColumn = head.getColumn();
        
        List<Vertex> inHeadRow = me.getBody().stream()
                                   .filter(node -> node.getRow() == headRow)
                                   .sorted(Comparator.comparingInt(v -> v.getColumn()))
                                   .collect(toList());
        
        if (inHeadRow.size() > 1) {
            int min = inHeadRow.get(0).getColumn();
            int max = inHeadRow.get(inHeadRow.size() - 1).getColumn();
            for (int i = min + 1; i < max; i++) {
                risks.add(new Vertex(headRow, i));
            }
        }
        
        List<Vertex> inHeadColumn = me.getBody().stream()
                                      .filter(node -> node.getColumn() == headColumn)
                                      .sorted(Comparator.comparingInt(v -> v.getRow()))
                                      .collect(toList());
        
        if (inHeadColumn.size() > 1) {
            int min = inHeadColumn.get(0).getRow();
            int max = inHeadColumn.get(inHeadColumn.size() - 1).getRow();
            for (int i = min + 1; i < max; i++) {
                risks.add(new Vertex(i, headColumn));
            }
        }
        
        return risks.stream().collect(toList());
        
    }
    
}
