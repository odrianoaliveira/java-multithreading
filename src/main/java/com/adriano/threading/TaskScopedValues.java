package com.adriano.threading;

public class TaskScopedValues implements Runnable{
    @Override
    public void run() {

    }

    private final static ScopedValue<String> scopedValue = ScopedValue.newInstance();
}
