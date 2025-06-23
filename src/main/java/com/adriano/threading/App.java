package com.adriano.threading;

public class App {

    public static void main(String[] args) {
//        System.out.println("Starting threads...");
//        for(int i = 0; i< 3; i++) {
//            var task = new TaskThread();
//            task.start();
//        }

        System.out.println("Starting runnables...");
        for(int i = 0; i< 3; i++) {
//            var taskPlatformThread = new Thread(new TaskRunnable()); //Platform thread
//            taskPlatformThread.start();
            var taskVirtualThread = Thread.startVirtualThread(new TaskRunnable());
            try {
                taskVirtualThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
