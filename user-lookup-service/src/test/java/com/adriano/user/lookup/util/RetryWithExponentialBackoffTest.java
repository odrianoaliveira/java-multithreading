package com.adriano.user.lookup.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RetryWithExponentialBackoffTest {

    @Test
    void returnsResultOnFirstTry() {
        RetryWithExponentialBackoff<String> retry = new RetryWithExponentialBackoff<>(3);
        String result = retry.executeWithRetry(() -> "success");
        assertEquals("success", result);
    }

    @Test
    void retriesAndSucceeds() {
        RetryWithExponentialBackoff<Integer> retry = new RetryWithExponentialBackoff<>(3);
        int[] attempts = {0};
        int result = retry.executeWithRetry(() -> {
            if (++attempts[0] < 2) throw new RetryWithExponentialBackoff.TransientException("fail");
            return 42;
        });
        assertEquals(42, result);
        assertEquals(2, attempts[0]);
    }

    @Test
    void throwsAfterMaxAttempts() {
        RetryWithExponentialBackoff<Void> retry = new RetryWithExponentialBackoff<>(2);
        assertThrows(RetryWithExponentialBackoff.TransientException.class, () ->
                retry.executeWithRetry(() -> {
                    throw new RetryWithExponentialBackoff.TransientException("fail");
                })
        );
    }
}