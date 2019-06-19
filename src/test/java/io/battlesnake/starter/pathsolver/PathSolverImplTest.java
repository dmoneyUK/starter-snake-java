package io.battlesnake.starter.pathsolver;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class PathSolverImplTest {
    
    @Test
    public void test() {
        Executor executor = Executors.newFixedThreadPool(4);
        
        List<CompletableFuture<Void>> futures =
                IntStream.range(0, 1000)
                         .mapToObj(i -> CompletableFuture.supplyAsync(() -> i, executor)
                                                         .thenAcceptAsync(
                                                                 k -> System.out.println(Thread.currentThread().getName() + ":" + k),
                                                                 executor))
                         .collect(Collectors.toList());
        
        
    }

    
}
