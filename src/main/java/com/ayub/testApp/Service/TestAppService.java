package com.ayub.testApp.Service;


import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class TestAppService {
    private final Map<String, Future<List<Integer>>> taskStorage = new ConcurrentHashMap<>();
    private final ThreadPoolTaskExecutor executor;

    public TestAppService() {
        this.executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.initialize();
    }

    @Async
    public CompletableFuture<String> startTask(int min, int max, int count) {
        String taskId = UUID.randomUUID().toString();
        Future<List<Integer>> future = executor.submit(() -> runTask(min, max, count));
        taskStorage.put(taskId, future);
        System.out.println("/The obj: id - " + taskId + "!");
        return CompletableFuture.completedFuture(taskId);
    }

    public List<Integer> getResult(String taskId) {
        Future<List<Integer>> future = taskStorage.get(taskId);
        if (future == null) {
            throw new NoSuchElementException("Task not found");
        }
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Task execution failed", e);
        }
    }

    private List<Integer> runTask(int min, int max, int count) {
        AtomicInteger counter = new AtomicInteger(0);
        return Stream.generate(() -> {
            counter.incrementAndGet();
            return (int) (Math.random() * max + min);
        }).takeWhile(n -> counter.get() < count).toList();
    }
}
