package io.battlesnake.starter.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.pathsolver.PathSolver;
import io.battlesnake.starter.pathsolver.PathSolverImpl;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static io.battlesnake.starter.utils.GameBoardUtils.initGameBoard;

@Slf4j
public class SnakeHandler {
    
    /**
     * For the ping request
     */
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Map<String, String> EMPTY = new HashMap<>();
    private static final PathSolver pathSolver = new PathSolverImpl();
    
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
            log.info("{} called with: {}", uri, req.body());
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
            snakeResponse.put("color", "#736CCB");
            snakeResponse.put("headType","silly");
            snakeResponse.put("tailType", "hook");
            log.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
            return snakeResponse;
        } catch (Exception e) {
            log.warn("Something went wrong!", e);
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
    public Map<String, String> move(JsonNode moveRequest) throws Exception {
        GameBoard gameBoard = initGameBoard(moveRequest);
        
        String nextStep = pathSolver.findNextStep(gameBoard);
        
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
    
}
