package io.battlesnake.starter.service.strategy;

import io.battlesnake.starter.model.Vertex;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
@EqualsAndHashCode
class StrategyTransitStage {
    enum Stage {
        INIT,
        STRAVING,
        HEALTHY,
        STRONG,
        WEAK,
        FOUND_FOOD,
        NO_FOOD,
        DECIDED,
        TRAPPED,
        NO_EXIT;
    }
    
    private Stage stage;
    private Vertex target;
    
}
