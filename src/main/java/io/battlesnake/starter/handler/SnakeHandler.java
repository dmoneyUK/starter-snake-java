package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.Snake;
import io.battlesnake.starter.pathsolver.FoodPathSolver;
import io.battlesnake.starter.pathsolver.PathSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnakeHandler {
    
    /**
     * For the ping request
     */
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    private static final PathSolver pathSolver = new FoodPathSolver();
    private static int[][] board;
    private static final int FOOD = 2;
    private static final int BLOCKED = 1;
    
    /**
     * Generic processor that prints out the request and response from the methods.
     *
     * @param req
     * @param res
     * @return
     */
    public Map<String, String> process(Request req, Response res) {
        try {
            JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
            String uri = req.uri();
            LOG.info("{} called with: {}", uri, req.body());
            Map<String, String> snakeResponse;
            if (uri.equals("/start")) {
                snakeResponse = start(parsedRequest);
            } else if (uri.equals("/ping")) {
                snakeResponse = ping();
            } else if (uri.equals("/move")) {
                snakeResponse = move(parsedRequest);
            } else if (uri.equals("/end")) {
                snakeResponse = end(parsedRequest);
            } else {
                throw new IllegalAccessError("Strange call made to the snake: " + uri);
            }
            LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
            return snakeResponse;
        } catch (Exception e) {
            LOG.warn("Something went wrong!", e);
            return null;
        }
    }
    
    /**
     * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
     * snake is still alive.
     *
     * @return an empty response.
     */
    public Map<String, String> ping() {
        return EMPTY;
    }
    
    /**
     * /start is called by the engine when a game is first run.
     *
     * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing the snake setup values.
     */
    public Map<String, String> start(JsonNode startRequest) {
        
        // init the board (2 more rows and columns for the borders.
        initBoard(startRequest.get("board"));
        
        Map<String, String> response = new HashMap<>();
        response.put("color", "Hex-Color-Code-String");
        return response;
    }
    
    /**
     * /move is called by the engine for each turn the snake has.
     *
     * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return a response back to the engine containing snake movement values.
     */
    public Map<String, String> move(JsonNode moveRequest) {
        resetBoard();
        int[] head = markSelf(moveRequest.get("you").findValue("body"));
        Optional<int[]> food = markFood(moveRequest.findValue("food"));
        
        String nextStep = pathSolver.findNextStep(board, food, head);
        
        Map<String, String> response = new HashMap<>();
        response.put("move", nextStep);
        return response;
    }
    
    /**
     * /end is called by the engine when a game is complete.
     *
     * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
     * @return responses back to the engine are ignored.
     */
    public Map<String, String> end(JsonNode endRequest) {
        Map<String, String> response = new HashMap<>();
        return response;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    private void initBoard(JsonNode boardNode) {
        board = new int[boardNode.get("width").asInt() + 2][boardNode.get("height").asInt() + 2];
    }
    
    private void resetBoard() {
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 1; i < board[0].length - 1; i++) {
            Arrays.fill(board[i], 0);
            board[i][0] = 1;
            board[i][board[0].length - 1] = 1;
        }
    }
    
    private int[] markSelf(JsonNode body) {
        
        JsonNode node;
        int y = 0;
        int x = 0;
        for (int i = body.size() - 1; i >= 0; i--) {
            node = body.get(i);
            y = node.get("y").asInt() + 1;
            x = node.get("x").asInt() + 1;
            markOccupied(y, x, BLOCKED);
        }
        return new int[]{y, x};
    }
    
    private void markOccupied(int y, int x, int reason) {
        board[y][x] = reason;
    }
    
    private Optional<int[]> markFood(JsonNode node) {
        Optional<int[]> food = Optional.empty();
        if (node.size() == 0) {
            int y = node.findValue("y").asInt() + 1;
            int x = node.findValue("x").asInt() + 1;
            markOccupied(y, x, FOOD);
            food = Optional.of(new int[]{y, x});
        }
        return food;
    }
}
