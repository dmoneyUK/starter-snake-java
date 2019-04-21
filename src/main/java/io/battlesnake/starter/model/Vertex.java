package io.battlesnake.starter.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vertex {
    
    private int row;
    private int column;
    
    @JsonSetter("y")
    public void setRow(int y) {
        this.row = y + 1;
    }
    
    @JsonSetter("x")
    public void setColumn(int x) {
        this.column = x + 1;
    }
}
