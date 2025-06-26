package com.adriano.user.lookup.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Implements retry logic with exponential backoff and jitter.
 * Jitter is used to randomize the delay between retries, as recommended to avoid
 * thundering herd problems.
 */
public class RetryWithExponentialBackoff<T> {
    private final int maxAttempts;
    private final int baseInMillis;

    public RetryWithExponentialBackoff(int maxAttempts) {
        baseInMillis = 100;
        this.maxAttempts = maxAttempts;
    }

    public T executeWithRetry(Supplier<T> operation) {
        TransientException lastException = null;
        for (int j = 1; j <= maxAttempts; j++) {
            try {
                return operation.get();
            } catch (TransientException ex) {
                lastException = ex;
                if (j >= maxAttempts) break;
                long cap = baseInMillis * (long) Math.pow(2, j - 1);
                long delay = ThreadLocalRandom.current().nextLong(0, cap);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
        throw lastException != null ? lastException : new TransientException("Unknown Error");
    }

    public static class TransientException extends RuntimeException {
        public TransientException(Throwable e) {
            super(e);
        }

        public TransientException(String message) {
            super(message);
        }
    }
}
