package com.adriano.primenumbers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class PrimeCounter {

    private static final Logger log = LoggerFactory.getLogger(PrimeCounter.class);
    private static final Double BLOCKING_COEFFICIENT = 0.9;

    public static void main(String[] args) {
        var numOfCores = Runtime.getRuntime().availableProcessors();
        log.info("Number of cores = {}", numOfCores);

        var poolSize = (int) (numOfCores / (1 - BLOCKING_COEFFICIENT));
        log.info("Pool size = {}", poolSize);

        log.info("Serial execution");
        run();

        log.info("Concurrent execution");
        runConcurrent(poolSize, 4);
    }

    private static void runConcurrent(int poolSize, int partitions) {
        var number = 1_000_000;
        var latch = new CountDownLatch(partitions);
        try (var executor = Executors.newFixedThreadPool(poolSize);) {
            int chunk = number / partitions;
            List<CompletableFuture<Integer>> futures = new ArrayList<CompletableFuture<Integer>>();
            for (int j = 0; j < partitions; j++) {
                final int lower = (j * chunk) + 1;
                final int upper = (lower + chunk) - 1;
                var future = CompletableFuture.supplyAsync(() -> {
                    var count = computeElapsedTime(() -> countPrimesInRange(lower, upper));
                    log.info("There are {} prime numbers between {} and {}.", count, lower, upper);
                    latch.countDown();
                    return count;
                }, executor);
                futures.add(future);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }
    }

    static void run() {
        var upper = 1_000_000;
        var count = computeElapsedTime(() -> countPrimesInRange(1, upper));
        log.info("There are {} prime numbers under {}.", count, upper);
    }

    static int countPrimesInRange(int lower, int upper) {
        int total = 0;
        for (int i = lower; i <= upper; i++)
            if (isPrime(i))
                total++;
        return total;
    }

    static boolean isPrime(int number) {
        if (number <= 1)
            return false;

        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (i % number == 0)
                return false;
        }

        return true;
    }

    static <T> T computeElapsedTime(Supplier<T> cmd) {
        T result;
        var start = System.nanoTime();

        try {
            result = cmd.get();
        } finally {
            var end = System.nanoTime();
            var elapsedTime = end - start;
            var elapsedMillis = elapsedTime / 1_000_000;
            log.info("Elapsed time {} millis", elapsedMillis);
        }

        return result;
    }
}
