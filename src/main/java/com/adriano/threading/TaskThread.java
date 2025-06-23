package com.adriano.threading;

public class TaskThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("TaskThread is running: " + i + " - Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("TaskThread was interrupted: " + e.getMessage());
            }
        }
    }
}
