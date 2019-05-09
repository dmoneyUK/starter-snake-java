package io.battlesnake.starter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snake {
    private static int[][] dirs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    private String id;
    private String name;
    private int health;
    private List<Vertex> body;
    
    public Vertex getHead(){
        return body.get(0);
    }
    
    public boolean isShortThan(Snake other) {
        return body.size() < other.getBody().size();
    }
    
    public List<Vertex> getMovementRange() {
        return Arrays.stream(dirs)
                     .map(dir -> Vertex.builder()
                                       .row(dir[0] + getHead().getRow())
                                       .column(dir[1] + getHead().getColumn())
                                       .build())
                     .collect(Collectors.toList());
    }
    
    
}
