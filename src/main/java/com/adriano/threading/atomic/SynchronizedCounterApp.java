package com.adriano.threading.atomic;

import java.util.stream.IntStream;

/**
 * High-Throughput Counter
 * <p>
 * Replace a synchronized counter with AtomicInteger (or LongAdder) under 100 threads incrementing until 1 million.
 * <p>
 * Measure throughput gains.
 */
public class SynchronizedCounterApp {

    public static final int NUM_THREADS = 100;
    private static final int INCREMENTS_PER_THREAD = 10_000;

    private static final SynchronizedCounter counter = new SynchronizedCounter();

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        runThreads();
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Elapsed time in millis " + (elapsedNanos / 1_000_000));

        assert counter.getCounter() == NUM_THREADS * INCREMENTS_PER_THREAD;
        System.out.println("The counter value is " + counter.getCounter());
    }

    private static void runThreads() {
        IntStream.range(0, NUM_THREADS)
                .mapToObj(threadNum -> new Thread(new Counter(counter), "CounterThread-" + threadNum))
                .peek(Thread::start)
                .forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    static class Counter implements Runnable {
        private final SynchronizedCounter counter;

        Counter(SynchronizedCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            for (int x = 0; x < INCREMENTS_PER_THREAD; x++) {
                counter.increment();
            }
        }
    }

    static class SynchronizedCounter {
        private static long counter = 0;

        public synchronized void increment() {
            counter++;
            System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter.");
        }

        public synchronized long getCounter() {
            return counter;
        }
    }
}

