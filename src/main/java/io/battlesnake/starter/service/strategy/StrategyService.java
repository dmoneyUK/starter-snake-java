package io.battlesnake.starter.service.strategy;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;
import io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static io.battlesnake.starter.service.strategy.StrategyFn.chaseTailStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.eagerFoodCheck;
import static io.battlesnake.starter.service.strategy.StrategyFn.findNearestFoodStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.goEmptyNeighberStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.goFurthestStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.goRiskyFurthestStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.lenghtCheck;
import static io.battlesnake.starter.service.strategy.StrategyFn.safeGuardStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.stealOthersFoodStrategy;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.DECIDED;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.FOUND_FOOD;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.FOUND_FURTHEST;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.HEALTHY;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.INIT;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NO_EXIT;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NO_SAFE_EXIT;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NO_FOOD;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.STRAVING;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.STRONG;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.CANNOT_REACH_TAIL;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.WEAK;

@Slf4j
public class StrategyService {
    
    private StageTransitRule[] strategySelectTable = {
            
            new StageTransitRule(INIT, eagerFoodCheck, STRAVING, HEALTHY),
            new StageTransitRule(HEALTHY, lenghtCheck, STRONG, WEAK),
            new StageTransitRule(STRAVING, findNearestFoodStrategy, FOUND_FOOD, NO_FOOD),
            new StageTransitRule(WEAK,stealOthersFoodStrategy, FOUND_FOOD, NO_FOOD),
            new StageTransitRule(STRONG, chaseTailStrategy, DECIDED, CANNOT_REACH_TAIL),
            new StageTransitRule(FOUND_FOOD, safeGuardStrategy, DECIDED, NO_FOOD),
            new StageTransitRule(NO_FOOD, chaseTailStrategy, DECIDED, CANNOT_REACH_TAIL),
            new StageTransitRule(CANNOT_REACH_TAIL, goFurthestStrategy, FOUND_FURTHEST, NO_SAFE_EXIT),
            new StageTransitRule(FOUND_FURTHEST, safeGuardStrategy, DECIDED, NO_SAFE_EXIT),
            new StageTransitRule(NO_SAFE_EXIT, goRiskyFurthestStrategy, DECIDED, NO_EXIT),
            new StageTransitRule(NO_EXIT, goEmptyNeighberStrategy, DECIDED, DECIDED),
            
            // TODO use hamilton instead of furthest
        
    };
    
    public Vertex makeDecision(GameBoard gameBoard, Map<Vertex, int[][]> snakesDistanceBoardMap) {
        GameState gameState = GameState.builder()
                                       .gameBoard(gameBoard)
                                       .snakesDistanceBoardMap(snakesDistanceBoardMap)
                                       .build();
        
        StrategyTransitStage current = StrategyTransitStage.builder()
                                                           .stage(INIT)
                                                           .build();
        
        while (!DECIDED.equals(current.getStage())) {
            for (StageTransitRule stageTransitRule : strategySelectTable) {
                if (current.getStage().equals(stageTransitRule.entry)) {
                    log.info(current.getStage().toString());
                    current = execute(gameState, Optional.ofNullable(current.getTarget()), stageTransitRule.strategy,
                                      stageTransitRule.successExit,
                                      stageTransitRule.failureExit);
                    break;
                }
            }
        }
        
        return current.getTarget();
    }
    
    private StrategyTransitStage execute(GameState gameState, Optional<Vertex> previousTarget,
                                         BiFunction<GameState, Optional, StrategyResult> strategy,
                                         Stage successExit, Stage failureExit) {
        StrategyResult result = strategy.apply(gameState, previousTarget);
       
        return result.getSuccess() ? StrategyTransitStage.builder().stage(successExit).target(result.getTarget()).build() :
                StrategyTransitStage.builder().stage(failureExit).build();
        
    }
    
    @AllArgsConstructor
    private class StageTransitRule {
        private Stage entry;
        private BiFunction strategy;
        private Stage successExit;
        private Stage failureExit;
    }
    
}
