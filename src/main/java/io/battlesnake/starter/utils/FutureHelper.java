package io.battlesnake.starter.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class FutureHelper {
    
    public static <T> T getFromFuture(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Error happened when getFromFuture. Exception: {}", e);
        }
        return null;
    }
}
