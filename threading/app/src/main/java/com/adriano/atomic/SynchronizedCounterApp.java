package com.adriano.threading.atomic;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * High-Throughput Counter
 * <p>
 * Replace a synchronized counter with AtomicInteger (or LongAdder) under 100 threads incrementing until 1 million.
 * <p>
 * Measure throughput gains.
 */
public class SynchronizedCounterApp {

    public static final int NUM_THREADS = 1000;
    private static final int INCREMENTS_PER_THREAD = 10_000;

    public static void main(String[] args) {
        // Synchronized
        var synchronizedCounter = new SynchronizedCounter();
        long startTime = System.nanoTime();
        runSynchronizedThreads(synchronizedCounter);
        long elapsedNanos = System.nanoTime() - startTime;
        System.out.println("Elapsed time in millis " + (elapsedNanos / 1_000_000));

        if (!(synchronizedCounter.getCounter() == NUM_THREADS * INCREMENTS_PER_THREAD)) {
            System.err.println("The SynchronizedCounter has a bug");
        }
        System.out.println("The counter value is " + synchronizedCounter.getCounter());

        // Atomic
        var atomicCounter = new AtomicCounter();
        long startTime2 = System.nanoTime();
        runAtomicThreads(atomicCounter);
        long elapsedNanos2 = System.nanoTime() - startTime2;
        System.out.println("Elapsed time in millis " + (elapsedNanos2 / 1_000_000));

        if (!(atomicCounter.getCounter() == NUM_THREADS * INCREMENTS_PER_THREAD)) {
            System.err.println("The AtomicCounter has a bug");
        }
        System.out.println("The counter value is " + atomicCounter.getCounter());
    }

    private static void runAtomicThreads(ICounter counter) {
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

    private static void runSynchronizedThreads(ICounter counter) {
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
        private final ICounter counter;

        Counter(ICounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            for (int x = 0; x < INCREMENTS_PER_THREAD; x++) {
                counter.increment();
            }
        }
    }

    interface ICounter {
        void increment();

        long getCounter();
    }

    static class AtomicCounter implements ICounter {
        private static final AtomicLong counter = new AtomicLong(0);

        @Override
        public void increment() {
            counter.incrementAndGet();
        }

        @Override
        public long getCounter() {
            return counter.get();
        }
    }

    static class SynchronizedCounter implements ICounter {
        private static long counter = 0;

        @Override
        public synchronized void increment() {
            counter++;
        }

        @Override
        public synchronized long getCounter() {
            return counter;
        }
    }
}

