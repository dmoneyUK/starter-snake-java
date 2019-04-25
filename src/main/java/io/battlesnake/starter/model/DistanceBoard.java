package io.battlesnake.starter.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DistanceBoard implements Cloneable{
    private int[][] distance;
    private Vertex farthest;
}
