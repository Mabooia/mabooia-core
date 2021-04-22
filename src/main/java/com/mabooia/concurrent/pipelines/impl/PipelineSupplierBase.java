package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.concurrent.Mutex;
import com.mabooia.concurrent.pipelines.PipelineSupplier;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class PipelineSupplierBase<A> extends PipelineBase implements PipelineSupplier<A> {

    private final int bufferSize;
    private final Mutex consumingMutex = new Mutex(false);
    private final ConcurrentLinkedQueue<A> resultQueue = new ConcurrentLinkedQueue<>();

    protected PipelineSupplierBase(final ExecutorService executorService,
                                   final int bufferSize) {
        super(executorService);
        this.bufferSize = Math.max(1, bufferSize);
    }

    @Override
    public void cancel() {
        resultQueue.clear();
        super.cancel();
    }

    @Override
    public void get(final Consumer<A> onPublish) {
        if (!cancelled.get() && !stopNotified.get()) {
            mutex.release();
            executorService.submit(() -> {
                Optional<A> headOptional;
                while ((headOptional = Optional.ofNullable(resultQueue.poll())).isEmpty()
                    && !cancelled.get()
                    && !stopNotified.get()) {
                    consumingMutex.acquire();
                }
                headOptional.ifPresent(onPublish);
            });
        }
    }

    @Override
    protected Future<?> startTask() {
        return executorService.submit(() -> {
            mutex.acquire();
            Optional<A> next;
            while (!cancelled.get() && (next = getNext()).isPresent()) {
                resultQueue.add(next.get());
                consumingMutex.release();
                while (resultQueue.size() >= bufferSize) {
                    mutex.acquire();
                }
            }
            notifyStop();
        });
    }

    protected abstract Optional<A> getNext();
}
