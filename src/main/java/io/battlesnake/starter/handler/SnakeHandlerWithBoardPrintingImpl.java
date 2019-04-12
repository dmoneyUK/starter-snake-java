package io.battlesnake.starter.handler;

import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * SnakeHandler class for dealing with the routes set up in the main method.
 */
public class SnakeHandlerWithBoardPrintingImpl extends SnakeHandlerDecorator {
    
    public SnakeHandlerWithBoardPrintingImpl(SnakeHandler snakeHandler) {
        super(snakeHandler);
    }
    
    
    @Override
    public Map<String, String> process(Request req, Response res) {
        Map<String, String> result = super.process(req, res);
        //printBoard(super.getBoard());
        return result;

    }
    
    //    return super.move(moveRequest);
    //public Map<String, String> move(JsonNode moveRequest) {
    //@Override
    //
    //}
    //    return start;
    //    Map<String, String> start = super.start(startRequest);
    //public Map<String, String> start(JsonNode startRequest) {
    //@Override
    //
    //}
    //    return super.ping();
    //public Map<String, String> ping() {
    //@Override
    //
    
    //}
    //    return null;
    //public Map<String, String> end(JsonNode endRequest) {
    //@Override
    
    //}
}
