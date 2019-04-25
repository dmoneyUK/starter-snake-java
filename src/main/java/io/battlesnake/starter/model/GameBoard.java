package io.battlesnake.starter.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameBoard implements Cloneable{
    private int[][] board;
    private List<Snake> snakes;
    private List<Vertex> foodList;
    private Snake me;
    
}
