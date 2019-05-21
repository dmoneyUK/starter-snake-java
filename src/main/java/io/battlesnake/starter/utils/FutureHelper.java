package io.battlesnake.starter.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Slf4j
public class FutureHelper {
    
    public static Executor executor = Executors.newFixedThreadPool(10);
    
    public static <T> CompletableFuture<T> asyncExecute(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }
    
    public static <T> T getFromFuture(CompletableFuture<T> future) {
        return future.join();
    }
}
