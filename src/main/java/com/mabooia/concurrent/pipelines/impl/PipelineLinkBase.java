package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.concurrent.Tasks;
import com.mabooia.concurrent.pipelines.PipelineConsumer;
import com.mabooia.concurrent.pipelines.PipelineLink;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class PipelineLinkBase<M, R> extends PipelineBase implements PipelineLink<M, R> {

    private final ConcurrentLinkedQueue<M> messageQueue = new ConcurrentLinkedQueue<>();
    private final List<PipelineConsumer<R>> destinations = new LinkedList<>();

    protected PipelineLinkBase(final ExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void subscribe(PipelineConsumer<R> destination) {
        destinations.add(destination);
    }

    @Override
    public void push(final M msg) {
        messageQueue.add(msg);
        mutex.release();
    }

    protected abstract R processMessage(M msg);

    @Override
    protected Future<?> startTask() {
        return Tasks.async(executorService, () -> {
            mutex.acquire();
            while (!cancelled.get() && (!stopNotified.get() || !messageQueue.isEmpty())) {
                final List<Future<?>> subTasks = new LinkedList<>();
                while (!cancelled.get() && !messageQueue.isEmpty()) {
                    final M msg = messageQueue.poll();
                    subTasks.add(executorService.submit(() -> {
                        final R res = processMessage(msg);
                        destinations.forEach(d -> d.push(res));
                    }));
                }
                mutex.acquire();
                subTasks.forEach(Tasks::await);
            }
            destinations.forEach(PipelineConsumer::notifyStop);
            return null;
        });
    }
}

