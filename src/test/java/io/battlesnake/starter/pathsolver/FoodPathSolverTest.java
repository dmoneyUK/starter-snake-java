package io.battlesnake.starter.pathsolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

class FoodPathSolverTest {
    
    private FoodPathSolver testObj;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    
    @BeforeEach
    public void setUp() {
        testObj = new FoodPathSolver();
    }
    
}
