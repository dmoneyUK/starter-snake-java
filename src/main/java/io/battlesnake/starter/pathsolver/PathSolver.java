package io.battlesnake.starter.pathsolver;

import io.battlesnake.starter.model.GameBoard;
import io.battlesnake.starter.model.Vertex;

import java.util.List;
import java.util.Map;

public interface PathSolver {

    String findNextStep(GameBoard gameBoard);
}
