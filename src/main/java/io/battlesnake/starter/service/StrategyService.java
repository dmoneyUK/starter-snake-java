package io.battlesnake.starter.service;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;
import io.battlesnake.starter.service.strategy.GameState;
import io.battlesnake.starter.service.strategy.StrategyResult;
import io.battlesnake.starter.service.strategy.StrategyTransitStage;
import io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;

import static io.battlesnake.starter.service.strategy.StrategyFn.bigSnakeCheck;
import static io.battlesnake.starter.service.strategy.StrategyFn.chaseTailStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.eagerFoodCheck;
import static io.battlesnake.starter.service.strategy.StrategyFn.findNearestFoodStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.goEmptyNeighberStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.goFurthestStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.safeGuardStrategy;
import static io.battlesnake.starter.service.strategy.StrategyFn.smallSnakeCheck;
import static io.battlesnake.starter.service.strategy.StrategyFn.stealOthersFoodStrategy;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.BIG;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.CANNOT_REACH_TAIL;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.DECIDED;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.FOUND_FOOD;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.FOUND_FURTHEST;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.HEALTHY;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.INIT;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.MIDDLE;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NOT_SMALL;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NO_EXIT;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.NO_FOOD;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.SMALL;
import static io.battlesnake.starter.service.strategy.StrategyTransitStage.Stage.STARVING;

@Slf4j
public class StrategyService {
    
    private Random rd = new Random();
    private StageTransitRule[] strategySelectTable = {
        
            new StageTransitRule(INIT, eagerFoodCheck, STARVING, HEALTHY),
            new StageTransitRule(HEALTHY, smallSnakeCheck, SMALL, NOT_SMALL),
            new StageTransitRule(NOT_SMALL, bigSnakeCheck, BIG, MIDDLE),
            new StageTransitRule(STARVING, findNearestFoodStrategy, FOUND_FOOD, NO_FOOD),
            new StageTransitRule(SMALL, stealOthersFoodStrategy, FOUND_FOOD, NO_FOOD),
            new StageTransitRule(MIDDLE, goFurthestStrategy, FOUND_FURTHEST, NO_EXIT),
            new StageTransitRule(BIG, chaseTailStrategy, DECIDED, CANNOT_REACH_TAIL),
            new StageTransitRule(FOUND_FOOD, safeGuardStrategy, DECIDED, NO_FOOD),
            new StageTransitRule(NO_FOOD, goFurthestStrategy, DECIDED, NO_EXIT),
            new StageTransitRule(FOUND_FURTHEST, safeGuardStrategy, DECIDED, NO_EXIT),
            new StageTransitRule(NO_EXIT, chaseTailStrategy, DECIDED, CANNOT_REACH_TAIL),
            new StageTransitRule(CANNOT_REACH_TAIL, goEmptyNeighberStrategy, DECIDED, DECIDED),
            // TODO use hamilton instead of furthest
        
    };
    
    private StageTransitRule randomRuleForStrongStage() {
        return randomRules(new StageTransitRule(MIDDLE, goFurthestStrategy, FOUND_FURTHEST, NO_EXIT),
                           new StageTransitRule(MIDDLE, chaseTailStrategy, DECIDED, CANNOT_REACH_TAIL));
    }
    
    private StageTransitRule randomRules(StageTransitRule... options) {
        return options[rd.nextInt(options.length)];
    }
    
    public Optional<Vertex> makeDecision(GameBoard gameBoard, Map<Vertex, int[][]> snakesDistanceBoardMap) {
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
                    log.debug(current.getStage().toString());
                    current = execute(gameState, Optional.ofNullable(current.getTarget()), stageTransitRule.strategy,
                                      stageTransitRule.successExit,
                                      stageTransitRule.failureExit);
                    break;
                }
            }
        }
        
        return Optional.ofNullable(current.getTarget());
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
