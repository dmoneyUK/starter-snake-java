package io.battlesnake.starter.service.strategy;

import io.battlesnake.starter.model.Vertex;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StrategyResult {
    
    private Vertex target;
    public Boolean success;
    
    public static StrategyResult STRATEGY_FAILURE = StrategyResult.builder().success(false).build();
}
