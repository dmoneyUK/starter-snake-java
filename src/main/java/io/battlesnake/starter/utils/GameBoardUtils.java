package io.battlesnake.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Snake;
import io.battlesnake.starter.model.Vertex;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static io.battlesnake.starter.utils.FutureHelper.asyncExecute;
import static io.battlesnake.starter.utils.FutureHelper.getFromFuture;
import static java.util.stream.Collectors.toList;

@Slf4j
public class GameBoardUtils {
    
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    
    private static GameBoard createGameBoard(JsonNode request) {
        JsonNode boardNode = request.get("board");
        
        CompletableFuture<List<Vertex>> findFoodFuture = asyncFindAllFood(request);
        CompletableFuture<List<Snake>> findSnakesFuture = asyncFindSnakes(request);
        CompletableFuture<Snake> findMeFuture = asyncFindMe(request);
        
        return CompletableFuture.allOf(findFoodFuture, findSnakesFuture, findMeFuture)
                                .thenApply(v -> GameBoard.builder()
                                                         .board(new int[boardNode.get("width").asInt() + 2][boardNode.get("height")
                                                                                                                     .asInt() + 2])
                                                         .foodList(getFromFuture(findFoodFuture))
                                                         .snakes(getFromFuture(findSnakesFuture))
                                                         .me(getFromFuture(findMeFuture))
                                                         .build()).join();
    };
    
    public static GameBoard initGameBoard(JsonNode request) {
        GameBoard gameBoard = createGameBoard(request);
        CompletableFuture.allOf(asyncMarkBorders(gameBoard), asyncMarkSnakes(gameBoard), asyncMarkFood(gameBoard)).join();
        return gameBoard;
    
    }
    
    public static CompletableFuture<Snake> asyncFindMe(JsonNode request) {
        return asyncExecute(() -> {
            try {
                return JSON_MAPPER.treeToValue(request.get("you"), Snake.class);
            } catch (JsonProcessingException e) {
                log.error("Error happened when asyncFindMe(). Exception: {}", e);
            }
            return null;
        });
    }
    
    public static List<Vertex> findDangerous(GameBoard gameBoard) {
        return findHeadToHeadRiskFn(gameBoard);
        
    }
    
    public static int[][] getBoardClone(GameBoard gameBoard) {
    
        return Arrays.stream(gameBoard.getBoard())
                     .parallel()
                     .map(r -> r.clone())
                     .toArray(int[][]::new);
    }
    
    public static void markDangerous(int[][] board, Vertex dangerous) {
        board[dangerous.getRow()][dangerous.getColumn()] = 1;
    }
    
    public static Vertex findEmptyNeighberVertex(int[][] board, Vertex start) {
        for (int[] dir : dirs) {
            int y = start.getRow() + dir[0];
            int x = start.getColumn() + dir[1];
            if (board[y][x] != 1) {
                return Vertex.builder().row(y).column(x).build();
            }
        }
        throw new RuntimeException("Trapped!!!");
    }
    
    public static Optional<Vertex> findSafeVertexWithinTargetRound(int[][] distanceBoard, Vertex start) {
        if (distanceBoard[start.getRow()][start.getColumn()] != Integer.MAX_VALUE) {
            return Optional.of(start);
        }
        for (int[] dir : dirs) {
            int y = start.getRow() + dir[0];
            int x = start.getColumn() + dir[1];
            if (distanceBoard[y][x] != Integer.MAX_VALUE && distanceBoard[y][x] != 0) {
                return Optional.of(Vertex.builder().row(y).column(x).build());
            }
        }
        return Optional.empty();
    }
    
    public static int[][] getMyGameBoard(GameBoard gameBoard) {
        
        Snake me = gameBoard.getMe();
        int[][] boardClone = GameBoardUtils.getBoardClone(gameBoard);
        
        //PrintingUtils.printBoard(boardClone);
        
        // If not grow in the next turn, current tail is safe.
        int length = me.getLength();
        if (length > 3 && (length < 5 || !me.getTail().equals(me.getBody().get(length - 2)))) {
            int headRow = me.getHead().getRow();
            int headColumn = me.getHead().getColumn();
            int tailRow = me.getTail().getRow();
            int tailColumn = me.getTail().getColumn();
        
            boardClone[tailRow][tailColumn] = 0;
            if (headRow == tailRow) { // when head and tail are on same row, the body behind the tail are safe.
                me.getBody()
                  .parallelStream()
                  .filter(v -> v.getRow() == headRow)
                  .filter(v -> headColumn > tailColumn && tailColumn > v.getColumn()
                          || headColumn < tailColumn && tailColumn < v.getColumn())
                  .forEach(v -> boardClone[v.getRow()][v.getColumn()] = 0);
            
            } else if (headColumn == tailColumn) { // when head and tail are on same column, the body behind the tail are safe.
                me.getBody()
                  .parallelStream()
                  .filter(v -> v.getColumn() == headColumn)
                  .filter(v -> headRow > tailRow && tailRow > v.getRow()
                          || headRow < tailRow && tailRow < v.getRow())
                  .forEach(v -> boardClone[v.getRow()][v.getColumn()] = 0);
            }
            
        }
        //PrintingUtils.printBoard(boardClone);
        return boardClone;
    }
    
    private static CompletableFuture<GameBoard> asyncMarkBorders(GameBoard gameBoard) {
        return asyncExecute(() -> {
            int[][] board = gameBoard.getBoard();
            Arrays.fill(board[0], 1);
            Arrays.fill(board[board.length - 1], 1);
            for (int i = 1; i < board[0].length - 1; i++) {
                Arrays.fill(board[i], 0);
                board[i][0] = 1;
                board[i][board[0].length - 1] = 1;
            }
            return gameBoard;
        });
        
    }
    
    private static CompletableFuture<GameBoard> asyncMarkSnakes(GameBoard gameBoard) {
        return asyncExecute(() -> {
            gameBoard.getSnakes()
                     .parallelStream()
                     .forEach(snake -> markSnake(gameBoard, snake));
            return gameBoard;
        });
    }
    
    private static Snake markSnake(GameBoard gameBoard, Snake snake) {
        List<Vertex> body = snake.getBody();
        Vertex tail = snake.getTail();
        int lastIndex = snake.getLength()-1;
        if (!snake.getMovementRange().contains(tail) && body.indexOf(tail) == lastIndex) {
            body = body.subList(0, lastIndex);
        }
        body.parallelStream()
            .forEach(vertex -> markOccupied(gameBoard.getBoard(), vertex, BLOCKED));
    
        return snake;
    }
    
    private static CompletableFuture<GameBoard> asyncMarkFood(GameBoard gameBoard) {
        return asyncExecute(() -> {
            gameBoard.getFoodList()
                     .parallelStream()
                     .filter(food -> gameBoard.getBoard()[food.getRow()][food.getColumn()] != 1)
                     .forEach(food -> markOccupied(gameBoard.getBoard(), food, FOOD));
            return gameBoard;
        });
       
    }
    
    private static Vertex markOccupied(int[][] board, Vertex vertex, int reason) {
        board[vertex.getRow()][vertex.getColumn()] = reason;
        return vertex;
    }
    
    private static CompletableFuture<List<Vertex>> asyncFindAllFood(JsonNode request) {
        return asyncExecute(() -> {
            List<Vertex> foodList = new ArrayList<>();
            Iterator<JsonNode> foodIt = request.findValue("food").elements();
            
            while (foodIt.hasNext()) {
                Vertex vertex = null;
                try {
                    vertex = JSON_MAPPER.treeToValue(foodIt.next(), Vertex.class);
                } catch (JsonProcessingException e) {
                    log.error("Error happened when asyncFindAllFood(). Exception: {}", e);
                }
                
                foodList.add(vertex);
            }
            return foodList;
        });
    }
    
    private static CompletableFuture<List<Snake>> asyncFindSnakes(JsonNode request) {
        return asyncExecute(() -> {
            List<Snake> snakes = new ArrayList<>();
            Iterator<JsonNode> snakeIt = request.findValue("snakes").elements();
            
            while (snakeIt.hasNext()) {
                try {
                    snakes.add(JSON_MAPPER.treeToValue(snakeIt.next(), Snake.class));
                } catch (JsonProcessingException e) {
                    log.error("Error happened when findSnake(). Exception: {}", e);
                }
            }
            return snakes;
        });
    }
    
    private static int getHeadToHeadDistance(Vertex me, Vertex head) {
        return Math.abs(head.getRow() - me.getRow()) + Math.abs(head.getColumn() - me.getColumn());
    }
    
    private static List<Vertex> findHeadToHeadRiskFn(GameBoard gameBoard) {
        Snake me = gameBoard.getMe();
        return gameBoard.getSnakes()
                        .stream()
                        .filter(snake -> getHeadToHeadDistance(me.getHead(), snake.getHead()) == 2)
                        .filter(snake -> !snake.isShortThan(me))
                        .map(Snake::getMovementRange)
                        .flatMap(moveRange -> moveRange.stream())
                        .collect(toList());
    }
    
    //private static List<Vertex> findSelfCollisionRisk(GameBoard gameBoard) {
    //    Snake me = gameBoard.getMe();
    //    Vertex head = me.getHead();
    //    Vertex tail = me.getTail();
    //
    //    Set<Vertex> risks = new HashSet<>();
    //
    //    int headRow = head.getRow();
    //    int headColumn = head.getColumn();
    //    int tailRow = tail.getRow();
    //    int tailColumn = tail.getColumn();
    //
    //    List<Vertex> inHeadRow = me.getBody().stream()
    //                               .filter(node -> node.getRow() == headRow)
    //                               .filter(node -> node.getRow() != tailRow && node.getColumn() != tailColumn)
    //                               .sorted(Comparator.comparingInt(v -> v.getColumn()))
    //                               .collect(toList());
    //
    //    if (inHeadRow.size() > 1) {
    //        int min = inHeadRow.get(0).getColumn();
    //        int max = inHeadRow.get(inHeadRow.size() - 1).getColumn();
    //        for (int i = min + 1; i < max; i++) {
    //            risks.add(new Vertex(headRow, i));
    //        }
    //    }
    //
    //    List<Vertex> inHeadColumn = me.getBody().stream()
    //                                  .filter(node -> node.getColumn() == headColumn)
    //                                  .filter(node -> node.getRow() != tailRow && node.getColumn() != tailColumn)
    //                                  .sorted(Comparator.comparingInt(v -> v.getRow()))
    //                                  .collect(toList());
    //
    //    if (inHeadColumn.size() > 1) {
    //        int min = inHeadColumn.get(0).getRow();
    //        int max = inHeadColumn.get(inHeadColumn.size() - 1).getRow();
    //        for (int i = min + 1; i < max; i++) {
    //            risks.add(new Vertex(i, headColumn));
    //        }
    //    }
    //
    //    return risks.stream().collect(toList());
    //
    //}
    
}
