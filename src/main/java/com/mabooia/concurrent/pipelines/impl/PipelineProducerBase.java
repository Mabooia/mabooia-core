package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.concurrent.pipelines.PipelineConsumer;
import com.mabooia.concurrent.pipelines.PipelineProducer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PipelineProducerBase<T> extends PipelineBase implements PipelineProducer<T> {

    private final List<PipelineConsumer<T>> destinations = new LinkedList<>();

    protected PipelineProducerBase(final ExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void subscribe(PipelineConsumer<T> destination) {
        destinations.add(destination);
    }

    protected abstract void executeLoop(final AtomicBoolean cancelled);

    protected void publish(final T res) {
        destinations.forEach(d -> d.push(res));
    }

    @Override
    protected Future<?> startTask() {
        return executorService.submit(() -> {
            mutex.acquire();
            executeLoop(cancelled);
            destinations.forEach(PipelineConsumer::notifyStop);
        });
    }
}
