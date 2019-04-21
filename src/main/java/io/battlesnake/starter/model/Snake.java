package io.battlesnake.starter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snake {
    
    private String id;
    private String name;
    private int health;
    private List<Vertex> body;
}
