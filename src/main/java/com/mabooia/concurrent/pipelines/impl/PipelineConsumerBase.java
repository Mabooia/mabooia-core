package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.concurrent.Tasks;
import com.mabooia.concurrent.pipelines.PipelineConsumer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class PipelineConsumerBase<T> extends PipelineBase implements PipelineConsumer<T> {

    private final ConcurrentLinkedQueue<T> messageQueue = new ConcurrentLinkedQueue<>();

    protected PipelineConsumerBase(final ExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void push(T message) {
        messageQueue.add(message);
        mutex.release();
    }

    protected abstract void acceptMessage(T msg);

    @Override
    protected Future<?> startTask() {
        return Tasks.async(executorService, () -> {
            mutex.acquire();
            while (!cancelled.get() && (!stopNotified.get() || !messageQueue.isEmpty())) {
                final List<Future<?>> subTasks = new LinkedList<>();
                while (!cancelled.get() && !messageQueue.isEmpty()) {
                    final T msg = messageQueue.poll();
                    subTasks.add(executorService.submit(() -> acceptMessage(msg)))
                }
                mutex.acquire();
                subTasks.forEach(Tasks::await);
            }
            return null;
        });
    }
}
