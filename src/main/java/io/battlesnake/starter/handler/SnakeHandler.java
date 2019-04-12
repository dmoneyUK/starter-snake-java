package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import spark.Request;
import spark.Response;

import java.util.Map;

public interface SnakeHandler {
    
    Map<String, String> process(Request req, Response res);
    
    Map<String, String> ping();
    
    Map<String, String> start(JsonNode startRequest);
    
    Map<String, String> move(JsonNode moveRequest);
    
    Map<String, String> end(JsonNode endRequest);
    
    int[][] getBoard();
}
