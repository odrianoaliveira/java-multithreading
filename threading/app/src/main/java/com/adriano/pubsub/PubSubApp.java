package com.adriano.threading.pubsub;

import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class PubSubApp {
    public static void main(String[] args) {
        var queue = new LinkedBlockingQueue<Integer>(1000);
        try (var executorService = Executors.newFixedThreadPool(3)) {
            var futures = Set.of(executorService.submit(new Producer(queue)),
                    executorService.submit(new Consumer(queue)),
                    executorService.submit(new Consumer(queue)));
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    System.out.println("Thread was interrupted: " + e.getMessage());
                } catch (ExecutionException e) {
                    System.out.println("Error executing thread:" + e.getMessage());
                }
            });
            executorService.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Producer implements Runnable {

        private final BlockingQueue<Integer> queue;

        public Producer(BlockingQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                for (int j = 0; j < 100; j++) {
                    queue.put(j);
                }
                System.out.println("Sending a poison pill...");
                queue.put(-1); //Send a poison pill to the first consumer
                queue.put(-1); //Send another poison pill to the second consumer
                System.out.println("Poison pills were sent.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<Integer> queue;

        Consumer(BlockingQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                int i;
                while ((i = queue.take()) != -1) {
                    System.out.printf("factorial(%d) = %d.%n", i, factorial(i));
                }
                System.out.println("Shutting down...");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static BigInteger factorial(int n) {
            BigInteger result = new BigInteger("1");
            for (long i = 2; i <= n; i++) {
                result = result.multiply(new BigInteger(String.valueOf(i)));
            }
            return result;
        }
    }
}
