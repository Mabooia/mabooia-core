package com.mabooia.concurrent;

import java.util.concurrent.Semaphore;

public final class Mutex {

    private final Semaphore semaphore;

    public Mutex() {
        this(false);
    }

    public Mutex(boolean released) {
        semaphore = new Semaphore(released ? 1 : 0);
    }

    public void acquire() {
        try {
            semaphore.acquire();
        }
        catch (InterruptedException ignore) { }
    }

    public void release() {
        semaphore.release();
    }
}
