//package io.battlesnake.starter.pathsolver;
//
//import io.battlesnake.starter.model.GameBoard;
//import io.battlesnake.starter.model.Snake;
//import io.battlesnake.starter.model.Vertex;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static io.battlesnake.starter.utils.DistanceBoardUtils.getAllSnakesDistanceBoards;
//import static io.battlesnake.starter.utils.DistanceBoardUtils.getFarthestVertex;
//
//public class TailChasingPathSolver {
//
//    public List<Vertex> findNextStep(GameBoard gameBoard) {
//        List<Vertex> path;
//        Snake me = gameBoard.getMe();
//        // Calculate the distance board for all snakes
//        Map<Vertex, int[][]> snakesDistanceMap = getAllSnakesDistanceBoards(gameBoard);
//        int[][] myDistanceBoard = snakesDistanceMap.get(me.getHead());
//
//        //Lock area strategy (Move out to the calling method)
//        int length = me.getBody().size();
//        if (me.getHealth() >= 20 && length >= 20) {
//            Vertex tail = me.getTail();
//            if (myDistanceBoard[tail.getRow()][tail.getColumn()] > length / 2) {
//                path = backTrackPath(tail, myDistanceBoard);
//            }else{
//                Optional<Vertex> farthestVertex = getFarthestVertex(myDistanceBoard);
//                path=backTrackPath(farthestVertex.orElseGet())
//            }
//        }
//
//        //return null;
//    }
//
//    private List<Vertex> backTrackPath(Vertex target, int[][] distance) {
//        int dis = distance[target.getRow()][target.getColumn()];
//        if (dis == Integer.MAX_VALUE) {
//            return null;
//        }
//        int y = target.getRow();
//        int x = target.getColumn();
//        List<Vertex> path = new ArrayList<>();
//        path.add(Vertex.builder().row(y).column(x).build());
//        while (dis > 1) {
//            dis--;
//            for (int[] dir : dirs) {
//                if (distance[y - dir[0]][x - dir[1]] == dis) {
//                    y -= dir[0];
//                    x -= dir[1];
//                    path.add(Vertex.builder().row(y).column(x).build());
//                    break;
//                }
//            }
//        }
//        return path;
//    }
//
//}
