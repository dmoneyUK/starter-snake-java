package io.battlesnake.starter.handler;

import com.fasterxml.jackson.databind.JsonNode;
import spark.Request;
import spark.Response;

import java.util.Map;

public abstract class SnakeHandlerDecorator implements SnakeHandler {
    
    private SnakeHandler handlerDecorated;
    
    public SnakeHandlerDecorator(SnakeHandler snakeHandler) {
        handlerDecorated = snakeHandler;
    }
    
    public Map<String, String> process(Request req, Response res) {
        return handlerDecorated.process(req, res);
    }
    
    public Map<String, String> ping() {
        return handlerDecorated.ping();
    }
    
    public Map<String, String> start(JsonNode startRequest) {
        return handlerDecorated.start(startRequest);
    }
    
    public Map<String, String> move(JsonNode moveRequest) {
        return handlerDecorated.move(moveRequest);
    }
    
    public Map<String, String> end(JsonNode endRequest) {
        return handlerDecorated.end(endRequest);
    }
    
    public int[][] getBoard(){
        return handlerDecorated.getBoard();
    }
}
