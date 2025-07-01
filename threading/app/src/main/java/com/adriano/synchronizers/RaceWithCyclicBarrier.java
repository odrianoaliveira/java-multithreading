package com.adriano.synchronizers;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * Challenge: Use `CyclicBarrier` to simulate a simple race with 3 runners
 *
 * Scenario:
 * You have 3 runner threads. Each runner prepares (sleeps for a random short
 * time to simulate preparation),
 * then waits at the starting line. Once all runners are ready, they start
 * running together (print a "started running" message) and then finish.
 *
 * Your tasks:
 * 1. Create 3 threads representing runners.
 * 2. Each runner should:
 *
 * * Sleep randomly between 1-3 seconds to simulate preparation.
 * * Wait at the starting line using `CyclicBarrier`.
 * * After all runners reach the barrier, print a message saying the race has
 * started.
 * * Then print a message that the runner has finished.
 *
 * 3. Use the barrier’s **barrier action** feature to print `"All runners are
 * ready. Race started!"` exactly once when all runners arrive.
 *
 * ### Key learning goals:
 *
 * * Understand how threads wait on the barrier.
 * * Use the barrier action to trigger a task after all threads arrive.
 * * See how threads resume after the barrier is passed.
 *
 */

public class RaceWithCyclicBarrier {

    private static final int NUM_RUNNERS = 3;
    private static final CyclicBarrier barrier = new CyclicBarrier(NUM_RUNNERS, new RaceManager());

    public static void main(String[] args) {
        var race = new RaceWithCyclicBarrier();
        race.run();
    }

    void run() {
        for (int i = 0; i < NUM_RUNNERS; i++) {
            var runner = new Thread(new Runner());
            runner.setName("Runner-%d".formatted(i));
            runner.start();
        }
    }

    static class RaceManager implements Runnable {
        private void start() {
            log("All runners are ready. Race started!");
        }

        @Override
        public void run() {
            start();
        }

    }

    static class Runner implements Runnable {

        @Override
        public void run() {
            var runner = Thread.currentThread().getName();
            log("%s is preparing himself.", runner);

            try {
                var randomPrepInMillis = ThreadLocalRandom.current().nextInt(1000);
                Thread.sleep(randomPrepInMillis);

                log("%s waits at the starting line.", runner);
                barrier.await();

                log("%s started running.", runner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void log(String message, Object... args) {
        System.out.println(message.formatted(args));
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
