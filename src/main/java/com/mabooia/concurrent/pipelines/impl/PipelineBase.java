package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.concurrent.Mutex;
import com.mabooia.concurrent.Tasks;
import com.mabooia.concurrent.pipelines.Pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PipelineBase implements Pipeline {

    protected final ExecutorService executorService;
    protected final Mutex mutex = new Mutex();
    protected final AtomicBoolean stopNotified = new AtomicBoolean(false);
    protected final AtomicBoolean cancelled = new AtomicBoolean(false);

    private final Future<?> task;

    protected PipelineBase(ExecutorService executorService) {
        this.executorService = executorService;
        this.task = startTask();
    }

    @Override
    public void cancel() {
        cancelled.set(true);
        mutex.release();
    }

    @Override
    public void notifyStop() {
        stopNotified.set(true);
        mutex.release();
    }

    @Override
    public void start() {
        mutex.release();
    }

    @Override
    public void await() {
        Tasks.await(task);
    }

    protected abstract Future<?> startTask();
}
