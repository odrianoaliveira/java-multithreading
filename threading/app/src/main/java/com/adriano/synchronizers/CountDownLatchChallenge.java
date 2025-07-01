package com.adriano.synchronizers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CountDownLatchChallenge
 * <p>
 * Challenge Description:
 * Implement a scenario where multiple worker threads perform some independent tasks in parallel,
 * and the main thread waits until all workers complete before proceeding.
 * <p>
 * What you need to do:
 * - Create a fixed number of worker threads (e.g., 3 or 5).
 * - Each worker should simulate work by sleeping for a random short duration.
 * - After completing its work, each worker must count down the latch.
 * - The main thread should wait on the latch until all workers have finished.
 * - Once all workers are done, the main thread prints a completion message.
 * <p>
 * Constraints:
 * - Use CountDownLatch properly to synchronize the threads.
 * - Avoid busy-waiting or using Thread.sleep in the main thread for synchronization.
 * - The number of workers should be configurable.
 * <p>
 * Expected Outcome:
 * When you run the program, you should see output from each worker indicating it has started and finished.
 * After all workers finish, the main thread prints "All workers finished. Main thread proceeding."
 * The main thread must not print this message before all workers complete their tasks.
 */
public class CountDownLatchChallenge {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchChallenge challenge = new CountDownLatchChallenge(3);
        challenge.startWorkers();
        challenge.waitForWorkers();
    }

    private final int numberOfWorkers;
    private final CountDownLatch latch;

    public CountDownLatchChallenge(int numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
        latch = new CountDownLatch(numberOfWorkers);
    }

    private void waitForWorkers() {
        try {
            System.out.println("Waiting for workers...");
            latch.await();
            System.out.println("All workers finished.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startWorkers() {
        for (int w = 0; w < numberOfWorkers; w++) {
            var worker = new Thread(new Worker(latch));
            worker.setName("Worker-" + w);
            worker.start();
        }
    }

    static class Worker implements Runnable {
        private final CountDownLatch latch;

        Worker(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            var workerName = Thread.currentThread().getName();
            System.out.printf("%s is busy.%n", workerName);
            var sleepTime = ThreadLocalRandom.current().nextInt(100, 500);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.printf("%s Done.%n", workerName);
            latch.countDown();
        }
    }
}

