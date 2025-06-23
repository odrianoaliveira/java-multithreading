package com.adriano.threading;

public class TaskRunnable implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("TaskRunnable is running: " + i + " - Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("TaskRunnable was interrupted: " + e.getMessage());
            }
        }
    }
}
