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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.battlesnake.starter.utils.GameBoardUtils.createGameBoard;
import static io.battlesnake.starter.utils.GameBoardUtils.getVertex;
import static io.battlesnake.starter.utils.GameBoardUtils.resetGameBoard;

public class SnakeHandler {
    
    /**
     * For the ping request
     */
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    private static final PathSolver pathSolver = new FoodPathSolver();
    
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
    
        createGameBoard(startRequest);
        
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
    
        int[][] board = resetGameBoard(moveRequest);
  
        markSankes(board, moveRequest.findValue("snakes").findValues("body"));
        Optional<int[]> food = markFood(board, moveRequest.findValue("food"));
        
        int[] head = findSelfHead(moveRequest.get("you").findValue("body"));
    
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
    
    
    
    private int[] findSelfHead(JsonNode body) {
        return getVertex(body);
    }
    
    private void markSankes(int[][] board, List<JsonNode> snakes) {
        snakes.parallelStream().forEach(body -> {
            int y;
            int x;
            for (int i = body.size() - 1; i >= 0; i--) {
                JsonNode node = body.get(i);
                int[] vertex = getVertex(node);
                markOccupied(board, vertex, BLOCKED);
            }
        });
    }
    
    private Optional<int[]> markFood(int[][] board, JsonNode node) {
        Optional<int[]> food = Optional.empty();
        if (node.size() != 0) {
            int[] foodVertex = getVertex(node);
            markOccupied(board, foodVertex, FOOD);
            food = Optional.of(foodVertex);
        }
        return food;
    }
    
    private void markOccupied(int[][] board, int[] vertex, int reason) {
        board[vertex[0]][vertex[1]] = reason;
    }
}
