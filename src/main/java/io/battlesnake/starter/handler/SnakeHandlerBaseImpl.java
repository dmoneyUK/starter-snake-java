package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.Snake;
import io.battlesnake.starter.pathsolver.FoodPathSolver;
import io.battlesnake.starter.pathsolver.PathSolver;
import io.battlesnake.starter.utils.PrintingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * SnakeHandler class for dealing with the routes set up in the main method.
 */
public class SnakeHandlerBaseImpl implements SnakeHandler {
    
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
        initBoard(startRequest.get("width").asInt() + 2, startRequest.get("height").asInt() + 2);
        
        Map<String, String> response = new HashMap<>();
        response.put("color", "#ff00ff");
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
        int[] head = markSelf(moveRequest.get("you").findValue("body").get("data"));
        //int[] food = markFood(parsedRequest.get("food").get("data"));
        int[] food = getFoodPosition(moveRequest.get("food").get("data"));
        //PrintingUtils.printBoard(board);
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
    
    private void initBoard(int height, int width) {
        board = new int[height][width];
    }
    
    private void resetBoard() {
        Arrays.fill(board[0], 1);
        Arrays.fill(board[board.length - 1], 1);
        for (int i = 0; i < board[0].length - 1; i++) {
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
    
    private int[] getFoodPosition(JsonNode food) {
        return new int[]{food.findValue("y").asInt() + 1, food.findValue("x").asInt() + 1};
    }
    
    private void markOccupied(int y, int x, int reason) {
        board[y][x] = reason;
    }
    //
    //private void updateBoard(JsonNode node) {
    //    resetBoard();
    //    markSelf(node.get("you").findValue("body").get("data"));
    //    markFood(node.get("food").get("data"));
    //}
    //
    //private int[] markFood(JsonNode node) {
    //
    //    int y = node.findValue("y").asInt() + 1;
    //    int x = node.findValue("x").asInt() + 1;
    //    markOccupied(y, x, FOOD);
    //    int[] food = {y, x};
    //    return food;
    //}
}
